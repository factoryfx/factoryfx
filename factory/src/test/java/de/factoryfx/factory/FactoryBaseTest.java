package de.factoryfx.factory;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FactoryBaseTest {

    public void create_loop_test(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttribute.set(exampleFactoryA);
            exampleFactoryA.referenceAttribute.set(exampleFactoryB);

            exampleFactoryA.internalFactory().loopDetector();
        });
    }

    @Test
    public void create_loop_test_doppelte_added_but_no_circle(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        ExampleFactoryB exampleFactoryB2 = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB2.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB2);

        exampleFactoryC.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internalFactory().loopDetector();
    }

    @Test
    public void test_collect_Live_Objects(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internalFactory().instance();

        //TODO fix test
//        HashMap<String, LiveObject> liveObjects = new HashMap<>();
//        exampleFactoryA.collectLiveObjects(liveObjects);
//
//        Assertions.assertEquals(3,liveObjects.entrySet().size());
    }

    public static class XRoot extends SimpleFactoryBase<String,XRoot> {
        public final FactoryReferenceAttribute<String,ExampleFactoryAndViewA> referenceAttribute = new FactoryReferenceAttribute<>(ExampleFactoryAndViewA.class).labelText("ExampleA2");
        public final FactoryReferenceAttribute<String,XFactory> xFactory = new FactoryReferenceAttribute<>(XFactory.class).labelText("XFactory");
        public final FactoryReferenceAttribute<String,XFactory> xFactory2 = new FactoryReferenceAttribute<>(XFactory.class).labelText("XFactory");
        public final FactoryReferenceListAttribute<String,XFactory> xFactoryList = new FactoryReferenceListAttribute<>(XFactory.class).labelText("XFactory");

        @Override
        public String createImpl() {
            referenceAttribute.instance();
            xFactory.instance();
            xFactory2.instance();
            xFactoryList.instances();
            return "1";
        }
    }

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<String,ExampleFactoryAndViewA> {
        public final FactoryViewReferenceAttribute<XRoot,String,XFactory> referenceView = new FactoryViewReferenceAttribute<XRoot,String,XFactory>(
                root -> root.xFactory.get()).labelText("ExampleA2");
        public final FactoryViewListReferenceAttribute<XRoot,String,XFactory> listView = new FactoryViewListReferenceAttribute<XRoot,String,XFactory>(
                root -> root.xFactoryList.get()).labelText("ExampleA2");

        @Override
        public String createImpl() {
            referenceView.instance();
            listView.instances();
            return "2";
        }
    }


    public static class XFactory extends SimpleFactoryBase<String,XFactory> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryReferenceAttribute<String,X2Factory> xFactory2 = new FactoryReferenceAttribute<>(X2Factory.class).labelText("XFactory");

        public List<String> createCalls=new ArrayList<>();


        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }
    }

    public static class X2Factory extends SimpleFactoryBase<String, XFactory> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryReferenceAttribute<String,X3Factory> xFactory3 = new FactoryReferenceAttribute<>(X3Factory.class).labelText("XFactory");


        public List<String> createCalls=new ArrayList<>();

        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }

        public X2Factory(){
            this.configLifeCycle().setUpdater(s -> {
                //dummy update
            });
        }
    }

    public static class X3Factory extends SimpleFactoryBase<String, XFactory> {
        public final StringAttribute bla=new StringAttribute();

        public List<String> createCalls=new ArrayList<>();

        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }

    }


    @Test
    public void test_determineRecreationNeed(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().addBackReferences();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy);
        usableCopy.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(usableCopy.needRecreation);
        Assertions.assertFalse(usableCopy.xFactory.get().needRecreation);
        Assertions.assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_determineRecreationNeed_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().addBackReferences();

        HashSet<Data> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(root.needRecreation);
        Assertions.assertFalse(root.xFactory.get().needRecreation);
        Assertions.assertTrue(root.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().addBackReferences();

        HashSet<Data> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        changed.add(root.xFactory.get());

        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(root.needRecreation);
        Assertions.assertTrue(root.xFactory.get().needRecreation);
        Assertions.assertTrue(root.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_changed_view(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().addBackReferences();

        Set<Data> changed = Set.of(root.xFactory.get());

        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(root.needRecreation);
        Assertions.assertTrue(root.xFactory.get().needRecreation);
        Assertions.assertTrue(root.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_changedDeep_viewlist(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactoryList.add(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().addBackReferences();
        root.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(root.xFactoryList.get().get(0));

        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(root.needRecreation);
        Assertions.assertTrue(root.xFactoryList.get(0).needRecreation);
        Assertions.assertTrue(root.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_determineRecreationNeed_recreate_only_once(){
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.xFactory2.set(root.xFactory.get());

        final XRoot usableCopy = root.internal().addBackReferences();
        usableCopy.internalFactory().instance();


        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.xFactory.get());
        usableCopy.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(usableCopy.needRecreation);
        Assertions.assertTrue(usableCopy.xFactory.get().needRecreation);

        usableCopy.xFactory.get().createCalls.clear();
        usableCopy.internalFactory().instance();
        Assertions.assertEquals(1,usableCopy.xFactory.get().createCalls.size());

    }

    public static class IterationTestFactory extends SimpleFactoryBase<Void,IterationTestFactory>{
        public String testinfo;
        public final FactoryReferenceListAttribute<Void,IterationTestFactory>  children = new FactoryReferenceListAttribute<>(IterationTestFactory.class);

        public IterationTestFactory(String testinfo) {
            this();
            this.testinfo = testinfo;
        }

        public IterationTestFactory() {
            super();
        }

        @Override
        public Void createImpl() {
            return null;
        }
    }

//https://google.github.io/guava/releases/22.0/api/docs/com/google/common/collect/TreeTraverser.html
//        h
//      / | \
//     /  e  \
//    d       g
//   /|\      |
//  / | \     f
// a  b  c

//postorder: abcdefgh
    @Test
    public void test_getFactoriesInCreateAndStartOrder(){
        IterationTestFactory root = new IterationTestFactory("h");

        IterationTestFactory d = new IterationTestFactory("d");
        root.children.add(d);
        root.children.add(new IterationTestFactory("e"));
        IterationTestFactory g = new IterationTestFactory("g");
        root.children.add(g);

        d.children.add(new IterationTestFactory("a"));
        d.children.add(new IterationTestFactory("b"));
        d.children.add(new IterationTestFactory("c"));

        g.children.add(new IterationTestFactory("f"));

        StringBuilder result = new StringBuilder();
        for (FactoryBase<?,?> item : root.internalFactory().getFactoriesInCreateAndStartOrder()) {
           result.append(((IterationTestFactory)item).testinfo);
        }
        Assertions.assertEquals("abcdefgh",result.toString());
    }



//        h
//      / | \
//     /  e  \
//    d       g
//   /|\      |
//  / | \     f
// a  b  c

    //breadth-first order: hdegabcf
    @Test
    public void test_getFactoriesInDestroyOrder(){
            IterationTestFactory root = new IterationTestFactory("h");

            IterationTestFactory d = new IterationTestFactory("d");
            root.children.add(d);
            root.children.add(new IterationTestFactory("e"));
            IterationTestFactory g = new IterationTestFactory("g");
            root.children.add(g);

            d.children.add(new IterationTestFactory("a"));
            d.children.add(new IterationTestFactory("b"));
            d.children.add(new IterationTestFactory("c"));

            g.children.add(new IterationTestFactory("f"));

            StringBuilder result = new StringBuilder();
            for (FactoryBase<?,?> item : root.internalFactory().getFactoriesInDestroyOrder()) {
                result.append(((IterationTestFactory)item).testinfo);
            }
            Assertions.assertEquals("hdegabcf",result.toString());

    }

    @Test
    public void test_update(){
        XRoot root = new XRoot();
        XFactory xFactory = new XFactory();
        root.xFactory.set(xFactory);
        xFactory.xFactory2.set(new X2Factory());

        root.internal().addBackReferences();


        HashSet<Data> changed =new HashSet<>();
        changed.add(root.xFactory.get().xFactory2.get());
        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertFalse(root.needRecreation);
        Assertions.assertFalse(root.xFactory.get().needRecreation);
        Assertions.assertTrue(root.xFactory.get().xFactory2.get().needRecreation);
    }

    @Test
    public void test_update_in_the_middle(){
        XRoot root = new XRoot();
        XFactory xFactory = new XFactory();
        root.xFactory.set(xFactory);
        X2Factory x2Factory = new X2Factory();
        xFactory.xFactory2.set(x2Factory);
        X3Factory x3Factory = new X3Factory();
        x2Factory.xFactory3.set(x3Factory);

        root.internal().addBackReferences();


        HashSet<Data> changed =new HashSet<>();
        changed.add(x3Factory);
        root.internalFactory().determineRecreationNeedFromRoot(changed);
        Assertions.assertFalse(root.needRecreation);
        Assertions.assertFalse(root.xFactory.get().needRecreation);
        Assertions.assertTrue(root.xFactory.get().xFactory2.get().needRecreation);
        Assertions.assertTrue(root.xFactory.get().xFactory2.get().xFactory3.get().needRecreation);
    }

    @Test
    public void test_copy_reflist_copied(){
        ExampleFactoryA original = new ExampleFactoryA();
        ExampleFactoryA copy = original.internal().copy();
        Assertions.assertFalse(original.referenceListAttribute==copy.referenceListAttribute);
    }


    @Test
    public void test_TreeBuilderName_survive_serilisation(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internalFactory().setTreeBuilderName("abc");

        ExampleFactoryA copy = ObjectMapperBuilder.build().copy(exampleFactoryA);
        Assertions.assertEquals("abc",copy.internalFactory().getTreeBuilderName());
    }

}