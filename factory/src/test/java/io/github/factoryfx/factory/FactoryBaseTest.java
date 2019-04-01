package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewListReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewReferenceAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleFactoryC;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

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

            exampleFactoryA.internal().loopDetector();
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

        exampleFactoryA.internal().loopDetector();
    }

    @Test
    public void test_collect_Live_Objects(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internal().instance();

        //TODO fix test
//        HashMap<String, LiveObject> liveObjects = new HashMap<>();
//        exampleFactoryA.collectLiveObjects(liveObjects);
//
//        Assertions.assertEquals(3,liveObjects.entrySet().size());
    }

    public static class XRoot extends SimpleFactoryBase<String,XRoot> {
        public final FactoryReferenceAttribute<XRoot,String,ExampleFactoryAndViewA> referenceAttribute = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<XRoot,String,XFactory> xFactory = new FactoryReferenceAttribute<>();
        public final FactoryReferenceAttribute<XRoot,String,XFactory> xFactory2 = new FactoryReferenceAttribute<>();
        public final FactoryReferenceListAttribute<XRoot,String,XFactory> xFactoryList = new FactoryReferenceListAttribute<>();

        @Override
        public String createImpl() {
            referenceAttribute.instance();
            xFactory.instance();
            xFactory2.instance();
            xFactoryList.instances();
            return "1";
        }
    }

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<String,XRoot> {
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


    public static class XFactory extends SimpleFactoryBase<String,XRoot> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryReferenceAttribute<XRoot,String,X2Factory> xFactory2 = new FactoryReferenceAttribute<>();

        public List<String> createCalls=new ArrayList<>();


        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }
    }

    public static class X2Factory extends SimpleFactoryBase<String, XRoot> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryReferenceAttribute<XRoot,String,X3Factory> xFactory3 = new FactoryReferenceAttribute<>();


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

    public static class X3Factory extends SimpleFactoryBase<String, XRoot> {
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
        usableCopy.internal().instance();

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(usableCopy);
        usableCopy.internal().determineRecreationNeedFromRoot(changed);
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        root.internal().determineRecreationNeedFromRoot(changed);
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        changed.add(root.xFactory.get());

        root.internal().determineRecreationNeedFromRoot(changed);
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

        Set<FactoryBase<?,?>> changed = Set.of(root.xFactory.get());

        root.internal().determineRecreationNeedFromRoot(changed);
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
        root.internal().instance();

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(root.xFactoryList.get().get(0));

        root.internal().determineRecreationNeedFromRoot(changed);
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
        usableCopy.internal().instance();


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(usableCopy.xFactory.get());
        usableCopy.internal().determineRecreationNeedFromRoot(changed);
        Assertions.assertTrue(usableCopy.needRecreation);
        Assertions.assertTrue(usableCopy.xFactory.get().needRecreation);

        usableCopy.xFactory.get().createCalls.clear();
        usableCopy.internal().instance();
        Assertions.assertEquals(1,usableCopy.xFactory.get().createCalls.size());

    }

    public static class IterationTestFactory extends SimpleFactoryBase<Void,IterationTestFactory>{
        public String testinfo;
        public final FactoryReferenceListAttribute<IterationTestFactory,Void,IterationTestFactory>  children = new FactoryReferenceListAttribute<>();

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
        for (FactoryBase<?,?> item : root.internal().getFactoriesInCreateAndStartOrder()) {
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
            for (FactoryBase<?,?> item : root.internal().getFactoriesInDestroyOrder()) {
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


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(root.xFactory.get().xFactory2.get());
        root.internal().determineRecreationNeedFromRoot(changed);
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


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(x3Factory);
        root.internal().determineRecreationNeedFromRoot(changed);
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
        exampleFactoryA.internal().setTreeBuilderName("abc");

        ExampleFactoryA copy = ObjectMapperBuilder.build().copy(exampleFactoryA);
        Assertions.assertEquals("abc",copy.internal().getTreeBuilderName());
    }

    @Test
    public void test_mock(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.utility().mock( f-> Mockito.mock(ExampleLiveObjectA.class));

        ExampleLiveObjectA instance = exampleFactoryA.internal().instance();
        Assertions.assertTrue(MockUtil.isMock(instance));
    }

    @Test
    public void test_mock_typesafe(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.utility().<ExampleFactoryA>mock(f-> {
            f.referenceAttribute.instance();
            return Mockito.mock(ExampleLiveObjectA.class);
        });
    }

    @Test
    public void test_mock_after_copy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.utility().mock( f-> Mockito.mock(ExampleLiveObjectA.class));

        exampleFactoryA = exampleFactoryA.utility().copy();
        ExampleLiveObjectA instance = exampleFactoryA.internal().instance();
        Assertions.assertTrue(MockUtil.isMock(instance));
    }
}