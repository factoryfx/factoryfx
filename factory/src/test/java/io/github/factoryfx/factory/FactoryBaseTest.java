package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.AttributeGroup;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.testfactories.*;
import io.github.factoryfx.factory.util.LanguageText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

        exampleFactoryA.internal().finalise();
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
        public final FactoryAttribute<XRoot,String,ExampleFactoryAndViewA> referenceAttribute = new FactoryAttribute<>();
        public final FactoryAttribute<XRoot,String,XFactory> xFactory = new FactoryAttribute<>();
        public final FactoryAttribute<XRoot,String,XFactory> xFactory2 = new FactoryAttribute<>();
        public final FactoryListAttribute<XRoot,String,XFactory> xFactoryList = new FactoryListAttribute<>();

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
        public final FactoryViewAttribute<XRoot,String,XFactory> referenceView = new FactoryViewAttribute<XRoot,String,XFactory>(
                root -> root.xFactory.get()).labelText("ExampleA2");
        public final FactoryViewListAttribute<XRoot,String,XFactory> listView = new FactoryViewListAttribute<XRoot,String,XFactory>(
                root -> root.xFactoryList.get()).labelText("ExampleA2");

        @Override
        public String createImpl() {
            referenceView.instance();
            listView.instances();
            return "2";
        }

        public ExampleFactoryAndViewA(){
            System.out.println(

            );
        }
    }


    public static class XFactory extends SimpleFactoryBase<String,XRoot> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryAttribute<XRoot,String,X2Factory> xFactory2 = new FactoryAttribute<>();

        public List<String> createCalls=new ArrayList<>();


        @Override
        public String createImpl() {
            createCalls.add("call");
            return "3";
        }
    }

    public static class X2Factory extends SimpleFactoryBase<String, XRoot> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryAttribute<XRoot,String,X3Factory> xFactory3 = new FactoryAttribute<>();


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

        final XRoot usableCopy = root.internal().finalise();
        usableCopy.internal().instance();

        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(usableCopy);
        usableCopy.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(usableCopy.needRecreation);
        assertFalse(usableCopy.xFactory.get().needRecreation);
        assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_determineRecreationNeed_2(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().finalise();

        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        root.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(root.needRecreation);
        assertFalse(root.xFactory.get().needRecreation);
        assertTrue(root.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_3(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().finalise();

        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(root.referenceAttribute.get());
        changed.add(root.xFactory.get());

        root.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(root.needRecreation);
        assertTrue(root.xFactory.get().needRecreation);
        assertTrue(root.referenceAttribute.get().needRecreation);
    }

    @Test
    public void test_changedDeep_changed_view(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().finalise();

        Set<FactoryBase<?,XRoot>> changed = Set.of(root.xFactory.get());

        root.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(root.needRecreation);
        assertTrue(root.xFactory.get().needRecreation);
        assertTrue(root.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_changedDeep_viewlist(){
        ExampleFactoryAndViewA exampleFactoryAndViewA = new ExampleFactoryAndViewA();
        XRoot root = new XRoot();
        root.xFactoryList.add(new XFactory());
        root.referenceAttribute.set(exampleFactoryAndViewA);

        root.internal().finalise();
        root.internal().instance();

        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(root.xFactoryList.get().get(0));

        root.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(root.needRecreation);
        assertTrue(root.xFactoryList.get(0).needRecreation);
        assertTrue(root.referenceAttribute.get().needRecreation);
    }


    @Test
    public void test_determineRecreationNeed_recreate_only_once(){
        XRoot root = new XRoot();
        root.xFactory.set(new XFactory());
        root.xFactory2.set(root.xFactory.get());

        root.internal().finalise();
        root.internal().instance();


        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(root.xFactory.get());
        root.internal().determineRecreationNeedFromRoot(changed);
        assertTrue(root.needRecreation);
        assertTrue(root.xFactory.get().needRecreation);

        root.xFactory.get().createCalls.clear();
        root.internal().instance();
        assertEquals(1,root.xFactory.get().createCalls.size());

    }

    public static class IterationTestFactory extends SimpleFactoryBase<Void,IterationTestFactory>{
        public String testinfo;
        public final FactoryListAttribute<IterationTestFactory,Void,IterationTestFactory> children = new FactoryListAttribute<>();

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

        root.internal().finalise();

        StringBuilder result = new StringBuilder();
        for (FactoryBase<?,?> item : root.internal().getFactoriesInCreateAndStartOrder()) {
           result.append(((IterationTestFactory)item).testinfo);
        }
        assertEquals("abcdefgh",result.toString());
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

        root.internal().finalise();

        StringBuilder result = new StringBuilder();
        for (FactoryBase<?,?> item : root.internal().getFactoriesInDestroyOrder()) {
            result.append(((IterationTestFactory)item).testinfo);
        }
        assertEquals("hdegabcf",result.toString());

    }

    @Test
    public void test_update(){
        XRoot root = new XRoot();
        XFactory xFactory = new XFactory();
        root.xFactory.set(xFactory);
        xFactory.xFactory2.set(new X2Factory());

        root.internal().finalise();


        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(root.xFactory.get().xFactory2.get());
        root.internal().determineRecreationNeedFromRoot(changed);
        assertFalse(root.needRecreation);
        assertFalse(root.xFactory.get().needRecreation);
        assertTrue(root.xFactory.get().xFactory2.get().needRecreation);
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

        root.internal().finalise();


        HashSet<FactoryBase<?,XRoot>> changed =new HashSet<>();
        changed.add(x3Factory);
        root.internal().determineRecreationNeedFromRoot(changed);
        assertFalse(root.needRecreation);
        assertFalse(root.xFactory.get().needRecreation);
        assertTrue(root.xFactory.get().xFactory2.get().needRecreation);
        assertTrue(root.xFactory.get().xFactory2.get().xFactory3.get().needRecreation);
    }

    @Test
    public void test_copy_reflist_copied(){
        ExampleFactoryA original = new ExampleFactoryA();
        ExampleFactoryA copy = original.internal().copy();
        assertFalse(original.referenceListAttribute==copy.referenceListAttribute);
    }


    @Test
    public void test_TreeBuilderName_survive_serilisation(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().setTreeBuilderName("abc");

        ExampleFactoryA copy = ObjectMapperBuilder.build().copy(exampleFactoryA);
        assertEquals("abc",copy.internal().getTreeBuilderName());
    }

    @Test
    public void test_mock(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.utility().mock( f-> Mockito.mock(ExampleLiveObjectA.class));

        ExampleLiveObjectA instance = exampleFactoryA.internal().instance();
        assertTrue(MockUtil.isMock(instance));
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
        assertTrue(MockUtil.isMock(instance));
    }

    @Test
    public void test_collectChildrenDeepFromNode_width_cycle(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);

        Set<FactoryBase<?, ExampleDataA>> list = exampleFactoryA.internal().collectionChildrenDeepFromNonFinalizedTree();
        assertEquals(2,list.size());
    }

    public static class ViewExampleFactory extends FactoryBase<Void,ViewExampleFactory> {

        public final FactoryViewAttribute<ViewExampleFactory,Void, ViewExampleFactory> view= new FactoryViewAttribute<>((root) -> {
            root.internal().collectionChildrenDeepFromNonFinalizedTree();
            return null;
        });

    }

    //simplified testcase form other project
    @Test
    public void test_collectChildrenDeepFromNode_in_view(){
        ViewExampleFactory exampleFactoryA = new ViewExampleFactory();
        exampleFactoryA.internal().finalise();

        Set<FactoryBase<?, ViewExampleFactory>> list = exampleFactoryA.internal().collectionChildrenDeepFromNonFinalizedTree();
        assertEquals(1,list.size());
    }


    @Test
    public void test_childrenCounter(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.referenceAttributeC.set(new ExampleDataC());

        exampleFactoryA.internal().finalise();

        Assertions.assertEquals(3,((FactoryBase<?,?>)exampleFactoryA).treeChildrenCounter);
    }

    @Test
    public void test_determineRecreationNeed_multiple(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        root.referenceAttribute.set(exampleFactoryB);

        root.internal().finalise();
        root.internal().instance();

        HashSet<FactoryBase<?,ExampleFactoryA>> changed =new HashSet<>();
        changed.add(exampleFactoryB);
        root.internal().determineRecreationNeedFromRoot(changed);

        FactoryBase<ExampleLiveObjectA, ExampleFactoryA> rootForTestVisibility = root;
        assertTrue(rootForTestVisibility.needRecreation);
        assertTrue(rootForTestVisibility.needRecreation);

        root.internal().instance();

        HashSet<FactoryBase<?,ExampleFactoryA>> nochange =new HashSet<>();
        root.internal().determineRecreationNeedFromRoot(nochange);
        assertFalse(rootForTestVisibility.needRecreation);
        assertFalse(rootForTestVisibility.needRecreation);
        root.internal().instance();

//        Assertions.assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    public class ExampleFactoryGroup extends SimpleFactoryBase<Void, io.github.factoryfx.factory.testfactories.ExampleFactoryA> {
        public final StringAttribute stringAttribute1= new StringAttribute().labelText("ExampleA1").nullable();
        public final StringAttribute stringAttribute2= new StringAttribute().labelText("ExampleA1").nullable();

        @Override
        public Void createImpl() {
            return null;
        }

        public ExampleFactoryGroup() {
            this.config().setAttributeListGroupedSupplier(attributes ->
                    List.of(
                            new AttributeGroup(new LanguageText("group1"),List.of(stringAttribute1)),
                            new AttributeGroup(new LanguageText("group2"),List.of(stringAttribute1))
                    ));

        }
    }


    @Test
    public void test_setAttributeListGroupedSupplier(){
        ExampleFactoryGroup exampleFactoryGroup = new ExampleFactoryGroup();
        exampleFactoryGroup.stringAttribute1.set("bla");
        List<AttributeGroup> attributeGroups = exampleFactoryGroup.internal().attributeListGrouped();
        assertEquals(2,attributeGroups.size());
        assertEquals("group1",attributeGroups.get(0).title.internal_getPreferred(Locale.ENGLISH));
        assertEquals(1,attributeGroups.get(0).group.size());
        assertEquals("bla",attributeGroups.get(0).group.get(0).get());
    }

    @Test
    public void test_childrenCounter_after_copy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());

        ExampleFactoryA copy = exampleFactoryA.utility().copy();
        Assertions.assertEquals(2,((FactoryBase<?,?>)copy).treeChildrenCounter);

    }

    @Test
    public void test_finalised_children_aftercopy(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        copy.internal().finalise();
        assertEquals(1, ((FactoryBase<?, ?>)copy).finalisedChildrenFlat.size());
        assertEquals(0, ((FactoryBase<?, ?>)copy.referenceAttribute.get()).finalisedChildrenFlat.size());

    }

    @Test
    public void test_visitChildFactoriesAndViewsFlatFixed()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);

        ((FactoryBase<?, ?>)exampleFactoryA).finalizeChildren();
        Assertions.assertEquals(1,((FactoryBase<?, ?>)exampleFactoryA).finalisedChildrenFlat.size());
    }


    @Test
    public void test_visitChildFactoriesAndViewsFlatFixed_double_call()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);

        {
            ((FactoryBase<?, ?>) exampleFactoryA).finalizeChildren();
            Assertions.assertEquals(1,((FactoryBase<?, ?>)exampleFactoryA).finalisedChildrenFlat.size());
        }

        {
            ((FactoryBase<?, ?>) exampleFactoryA).finalizeChildren();
            Assertions.assertEquals(1,((FactoryBase<?, ?>)exampleFactoryA).finalisedChildrenFlat.size());
        }
    }

    @Test
    public void test_visitChildFactoriesAndViewsFlat(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB factory = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(factory);
        exampleFactoryA.referenceListAttribute.add(factory);


        ((FactoryBase<?, ?>)exampleFactoryA).finalizeChildren();
        Assertions.assertEquals(1,((FactoryBase<?, ?>)exampleFactoryA).finalisedChildrenFlat.size());
    }

    @Test
    public void test_back_references_after_set_double()  {
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();

        ExampleFactoryB factory = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        factory.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(factory);

        ExampleFactoryB factoryB = new ExampleFactoryB();
        exampleFactoryC.referenceAttribute.set(factoryB);
        exampleFactoryA.referenceListAttribute.add(factoryB);

        exampleFactoryA.internal().finalise();
        assertEquals(2,factoryB.internal().getParents().size());
    }


    @Test
    public void test_root_deep()  {
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        root.referenceAttribute.set(factoryB);

        ExampleFactoryC factoryC = new ExampleFactoryC();
        factoryB.referenceAttributeC.set(factoryC);

        root.internal().setRootDeep(root);

        assertEquals(root,root.internal().getRoot());
        assertEquals(root,factoryB.internal().getRoot());
        assertEquals(root,factoryC.internal().getRoot());
    }

    @Test
    public void test_collectChildrenDeep(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        root.referenceAttribute.set(factoryB);
        root.internal().finalise();

        root.referenceListAttribute.add(root.referenceAttribute.get());

        assertEquals(2,root.internal().collectChildrenDeep().size());
    }


    @Test
    public void test_fixDuplicateFactories(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        root.referenceAttribute.set(factoryB);
        root.internal().finalise();

        ExampleFactoryA copy = root.utility().copy();

        root.referenceListAttribute.add(copy.referenceAttribute.get());

        assertEquals(3,root.internal().collectChildrenDeep().size());
        assertEquals(root.referenceListAttribute.get(0).getId(),root.referenceAttribute.get().getId());
        assertFalse(root.referenceListAttribute.get(0)==root.referenceAttribute.get());
        root.internal().fixDuplicateFactories();
        assertTrue(root.referenceListAttribute.get(0)==root.referenceAttribute.get());
        assertEquals(2,root.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_fixDuplicateFactories_keep_current_factory(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        root.referenceAttribute.set(factoryB);
        root.internal().finalise();
        root.internal().instance();

        ExampleFactoryA copy = root.utility().copy();



        root.referenceListAttribute.add(copy.referenceAttribute.get());

        ExampleFactoryB originalFactory = root.referenceAttribute.get();
        root.internal().fixDuplicateFactories();

        assertEquals(originalFactory,root.referenceListAttribute.get(0));
        assertEquals(originalFactory,root.referenceAttribute.get());

    }


}