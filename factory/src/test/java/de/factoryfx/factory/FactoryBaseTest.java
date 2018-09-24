package de.factoryfx.factory;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleFactoryB;
import de.factoryfx.factory.testfactories.ExampleFactoryC;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FactoryBaseTest {

    @Test(expected = IllegalStateException.class)
    public void create_loop_test(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internalFactory().loopDetector();
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
//        Assert.assertEquals(3,liveObjects.entrySet().size());
    }

    public static class XRoot extends SimpleFactoryBase<String,Void,XRoot> {
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

    public static class ExampleFactoryAndViewA extends SimpleFactoryBase<String,Void,ExampleFactoryAndViewA> {
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


    public static class XFactory extends SimpleFactoryBase<String,Void,XFactory> {
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
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertFalse(usableCopy.xFactory.get().needRecreation);
        Assert.assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_determineRecreationNeed_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().addBackReferences();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.referenceAttribute.get());
        usableCopy.internalFactory().determineRecreationNeedFromRoot(changed);
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertFalse(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().addBackReferences();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.referenceAttribute.get());
        changed.add(usableCopy.xFactory.get());

        usableCopy.internalFactory().determineRecreationNeedFromRoot(changed);
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_changed_view(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        final XRoot usableCopy = root.internal().addBackReferences();
        usableCopy.internalFactory().instance();

        HashSet<Data> changed =new HashSet<>();
        changed.add(usableCopy.xFactory.get());

        usableCopy.internalFactory().determineRecreationNeedFromRoot(changed);
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);
        Assert.assertTrue(usableCopy.referenceAttribute.get().needRecreation);
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
        Assert.assertTrue(root.needRecreation);
        Assert.assertTrue(root.xFactoryList.get(0).needRecreation);
        Assert.assertTrue(root.referenceAttribute.get().needRecreation);
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
        Assert.assertTrue(usableCopy.needRecreation);
        Assert.assertTrue(usableCopy.xFactory.get().needRecreation);

        usableCopy.xFactory.get().createCalls.clear();
        usableCopy.internalFactory().instance();
        Assert.assertEquals(1,usableCopy.xFactory.get().createCalls.size());

    }

    public static class IterationTestFactory extends SimpleFactoryBase<Void,Void,IterationTestFactory>{
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
        for (FactoryBase<?, ?, ?> item : root.internalFactory().getFactoriesInCreateAndStartOrder()) {
           result.append(((IterationTestFactory)item).testinfo);
        }
        Assert.assertEquals("abcdefgh",result.toString());
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
            for (FactoryBase<?, ?, ?> item : root.internalFactory().getFactoriesInDestroyOrder()) {
                result.append(((IterationTestFactory)item).testinfo);
            }
            Assert.assertEquals("hdegabcf",result.toString());

    }

    @Test
    public void test_treebuildername(){
        ExampleFactoryA factory = new ExampleFactoryA();
        factory.internalFactory().setTreeBuilderName("EEE");
        ExampleFactoryA copy=ObjectMapperBuilder.build().copy(factory);
        Assert.assertEquals("EEE",factory.internalFactory().getTreeBuilderName());

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(factory));

    }
}