package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.attribute.AttributeGroup;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
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
    @Test
    public void test_loop_(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttribute.set(exampleFactoryA);
            exampleFactoryA.referenceAttribute.set(exampleFactoryB);

//        exampleFactoryA.internal().instance();
//        exampleFactoryB.internal().instance();
//
//
//        exampleFactoryA.internal().finalise();
//
//        exampleFactoryA.internal().collectChildrenDeep();
//        exampleFactoryA.internal().copy();
//        exampleFactoryA.internal().fixDuplicateFactories();
//        exampleFactoryA.internal().getFactoriesInCreateAndStartOrder();
//
//
//        ObjectMapperBuilder.build().writeValueAsString(exampleFactoryA);

//        exampleFactoryA.internal().determineRecreationNeedFromRoot(Set.of(exampleFactoryB));
            exampleFactoryA.internal().finalise();
            exampleFactoryA.internal().loopDetector();

        });
    }

    @Test
    public void test_loop_double(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(root);
        root.referenceAttribute.set(exampleFactoryB);
        root.internal().finalise();

        Assertions.assertThrows(IllegalStateException.class, () -> {
            root.internal().loopDetector();
        });
        root.referenceAttribute.set(null);
        root.internal().loopDetector();
    }

    @Test
    public void create_loop_test_twice_added_but_no_circle(){
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
        public final FactoryAttribute<String,ExampleFactoryAndViewA> referenceAttribute = new FactoryAttribute<>();
        public final FactoryAttribute<String,XFactory> xFactory = new FactoryAttribute<>();
        public final FactoryAttribute<String,XFactory> xFactory2 = new FactoryAttribute<>();
        public final FactoryListAttribute<String,XFactory> xFactoryList = new FactoryListAttribute<>();

        @Override
        protected String createImpl() {
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
        protected String createImpl() {
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
        public final FactoryAttribute<String,X2Factory> xFactory2 = new FactoryAttribute<>();

        public List<String> createCalls=new ArrayList<>();


        @Override
        protected String createImpl() {
            createCalls.add("call");
            return "3";
        }
    }

    public static class X2Factory extends SimpleFactoryBase<String, XRoot> {
        public final StringAttribute bla=new StringAttribute();
        public final FactoryAttribute<String,X3Factory> xFactory3 = new FactoryAttribute<>();


        public List<String> createCalls=new ArrayList<>();

        @Override
        protected String createImpl() {
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
        protected String createImpl() {
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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

        Set<FactoryBase<?,?>> changed = Set.of(root.xFactory.get());

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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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
        public final FactoryListAttribute<Void,IterationTestFactory> children = new FactoryListAttribute<>();

        public IterationTestFactory(String testinfo) {
            this();
            this.testinfo = testinfo;
        }

        public IterationTestFactory() {
            super();
        }

        @Override
        protected Void createImpl() {
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


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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


        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
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
    public void test_copy_root(){
        ExampleFactoryA original = new ExampleFactoryA();
        ExampleFactoryA copy = original.internal().copy();
        assertFalse(original==copy);
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

        Set<FactoryBase<?,ExampleDataA>> list = exampleFactoryA.internal().collectionChildrenDeepFromNonFinalizedTree();
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

        Set<FactoryBase<?,ViewExampleFactory>> list = exampleFactoryA.internal().collectionChildrenDeepFromNonFinalizedTree();
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

        HashSet<FactoryBase<?,?>> changed =new HashSet<>();
        changed.add(exampleFactoryB);
        root.internal().determineRecreationNeedFromRoot(changed);

        FactoryBase<ExampleLiveObjectA, ExampleFactoryA> rootForTestVisibility = root;
        assertTrue(rootForTestVisibility.needRecreation);
        assertTrue(rootForTestVisibility.needRecreation);

        root.internal().instance();

        HashSet<FactoryBase<?,?>> nochange =new HashSet<>();
        root.internal().determineRecreationNeedFromRoot(nochange);
        assertFalse(rootForTestVisibility.needRecreation);
        assertFalse(rootForTestVisibility.needRecreation);
        root.internal().instance();

//        Assertions.assertFalse(usableCopy.referenceAttribute.get().needRecreation);
    }

    public static class ExampleFactoryGroup extends SimpleFactoryBase<Void, io.github.factoryfx.factory.testfactories.ExampleFactoryA> {
        public final StringAttribute stringAttribute1= new StringAttribute().labelText("ExampleA1").nullable();
        public final StringAttribute stringAttribute2= new StringAttribute().labelText("ExampleA1").nullable();

        @Override
        protected Void createImpl() {
            return null;
        }

        public ExampleFactoryGroup() {
            this.config().setAttributeListGroupedSupplier(attributeAndMetadataCreator ->
                    List.of(
                            new AttributeGroup(new LanguageText("group1"),List.of(attributeAndMetadataCreator.apply(stringAttribute1))),
                            new AttributeGroup(new LanguageText("group2"),List.of(attributeAndMetadataCreator.apply(stringAttribute1)))
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
        assertEquals("bla",attributeGroups.get(0).group.get(0).attribute.get());
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

    @Test
    public void test_templateId_copy(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> new ExampleFactoryA());

        ExampleFactoryA exampleFactoryA = builder.buildTree();
        FactoryTemplateId<ExampleFactoryA> templateIdAfterBuild = new FactoryTemplateId<>(exampleFactoryA);
        Assertions.assertEquals(templateId,templateIdAfterBuild);

        ExampleFactoryA copy = exampleFactoryA.internal().copy();
        FactoryTemplateId<ExampleFactoryA> copyTemplateId = new FactoryTemplateId<>(copy);
        Assertions.assertEquals(templateId,copyTemplateId);
    }

    @Test
    public void test_attributeList(){
        ExampleFactoryA root = new ExampleFactoryA();
        List<AttributeAndMetadata> list = root.utility().attributeList(a -> a == root.stringAttribute);
        Assertions.assertEquals(1,list.size());
        Assertions.assertEquals(root.stringAttribute,list.get(0).attribute);
    }

    @Test
    public void test_attributeList_all(){
        ExampleFactoryA root = new ExampleFactoryA();
        List<AttributeAndMetadata> list = root.internal().attributeList();
        Assertions.assertEquals(3,list.size());
    }

    @Test
    public void test_semanticCopy_singleton(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            factoryA.referenceListAttribute.add(ctx.get(ExampleFactoryB.class));
            return factoryA;
        });
        builder.addSingleton(ExampleFactoryB.class,ctx->{
            return new ExampleFactoryB();
        });

        ExampleFactoryA root = builder.buildTree();
        Assertions.assertEquals(root.referenceAttribute.get(), root.referenceListAttribute.get(0));
        ExampleFactoryA copy = root.utility().semanticCopy();
        Assertions.assertEquals(copy.referenceAttribute.get(), copy.referenceListAttribute.get(0));

    }

    @Test
    public void test_semanticCopy_singleton_nested(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return factoryA;
        });
        builder.addSingleton(ExampleFactoryB.class,ctx->{
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttributeC.set(ctx.get(ExampleFactoryC.class));
            return exampleFactoryB;
        });
        builder.addPrototype(ExampleFactoryC.class,ctx->{
            return new ExampleFactoryC();
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();
        ExampleFactoryB copy = root.referenceAttribute.get().utility().semanticCopy();
        Assertions.assertNotEquals(root.referenceAttribute.get(), copy);
        Assertions.assertNotEquals(root.referenceAttribute.get().referenceAttributeC.get(), copy.referenceAttributeC.get());
    }

    @Test
    public void test_semanticCopy_singleton_stop_copy(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return factoryA;
        });
        builder.addSingleton(ExampleFactoryB.class,ctx->{
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttributeC.set(ctx.get(ExampleFactoryC.class));
            return exampleFactoryB;
        });
        builder.addPrototype(ExampleFactoryC.class,ctx->{
            return new ExampleFactoryC();
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();
        ExampleFactoryC expected = root.referenceAttribute.get().referenceAttributeC.get();
        ExampleFactoryA copy = root.utility().semanticCopy();
        Assertions.assertNotEquals(root, copy);
        Assertions.assertEquals(root.referenceAttribute.get(), copy.referenceAttribute.get());
        Assertions.assertEquals(expected, copy.referenceAttribute.get().referenceAttributeC.get());
    }


    @Test
    public void test_copy_semantic_copy_json_serialisation(){
        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> {
            ExampleFactoryA factoryA = new ExampleFactoryA();
            factoryA.referenceAttribute.set(ctx.get(ExampleFactoryB.class));
            return factoryA;
        });
        builder.addSingleton(ExampleFactoryB.class,ctx->{
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttributeC.set(ctx.get(ExampleFactoryC.class));
            return exampleFactoryB;
        });
        builder.addPrototype(ExampleFactoryC.class,ctx->{
            return new ExampleFactoryC();
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();
        ExampleFactoryB copy = root.referenceAttribute.get().utility().semanticCopy();
        ObjectMapperBuilder.build().copy(copy);
    }

    @Test
    public void test_collectChildrenFlat(){
        ExampleFactoryA root = new ExampleFactoryA();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        root.referenceAttribute.set(factoryB);
        root.internal().finalise();

        assertEquals(1,root.internal().collectChildrenFlat().size());
        assertEquals(factoryB,root.internal().collectChildrenFlat().get(0));
    }

    @Test
    public void test_copy_factoryTreeBuilder(){
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> treeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class,ctx->{
            return new ExampleFactoryA();
        });
        ExampleFactoryA original = treeBuilder.buildTree();

        ExampleFactoryA copy = original.internal().copy();
        assertNotNull(original.utility().getFactoryTreeBuilder(),"original");
        assertNotNull(copy.utility().getFactoryTreeBuilder(),"copy");

        //don't get factories from original treeBuilder
        assertEquals(copy, copy.utility().getFactoryTreeBuilder().buildSubTrees(ExampleFactoryA.class).get(0));
    }

    @Test
    public void test_semanticCopy_singleton_nonBuilder(){
        ExampleFactoryB factoryB = new ExampleFactoryB();

        ExampleFactoryA factoryA = new ExampleFactoryA();
        factoryA.referenceAttribute.set(factoryB);
        factoryA.referenceListAttribute.add(factoryB);

        ExampleFactoryA copy = factoryA.internal().finalise().utility().semanticCopy();
        Assertions.assertEquals(copy.referenceAttribute.get(), copy.referenceListAttribute.get(0));
    }

    @Test
    public void test_convert_nonBuilder(){

        FactoryTemplateId<ExampleFactoryA> templateId = new FactoryTemplateId<>(ExampleFactoryA.class,"root");
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(templateId, ctx -> new ExampleFactoryA());
        builder.addSingleton(ExampleFactoryB.class, ctx -> {
            ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
            exampleFactoryB.referenceAttributeC.set(ctx.get(ExampleFactoryC.class));
            return exampleFactoryB;
        });
        builder.addSingleton(ExampleFactoryC.class, ctx -> {
            return new ExampleFactoryC();
        });

        ExampleFactoryC factoryC = new ExampleFactoryC();
        ExampleFactoryB factoryB = new ExampleFactoryB();
        ExampleFactoryA factoryA = new ExampleFactoryA();
        factoryB.referenceAttributeC.set(factoryC);
        factoryA.referenceAttribute.set(factoryB);
        factoryA.internal().finalise();


        factoryA.internal().setFactoryTreeBuilder(builder);
        for (FactoryBase<?, ExampleFactoryA> factory : factoryA.internal().collectChildrenDeep()) {
            if (builder.getScope(new FactoryTemplateId<FactoryBase<?, ExampleFactoryA>>(null,factory.getClass()))!=null){
                factory.internal().setTreeBuilderClassUsed(true);
            }
        }

        ExampleFactoryB copyB = factoryA.referenceAttribute.get().utility().semanticCopy();
        factoryA.referenceListAttribute.add(copyB);
        Assertions.assertEquals(factoryA.referenceAttribute.get().referenceAttributeC.get(), factoryA.referenceListAttribute.get(0).referenceAttributeC.get());
    }


    @Test
    public void test_remove(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();
//        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
//        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());


        Assertions.assertEquals(exampleFactoryB,exampleFactoryA.internal().getRemoved().iterator().next());
    }

    @Test
    public void test_remove_null(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.referenceAttribute.set(null);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());


        Assertions.assertTrue(exampleFactoryA.internal().getRemoved().isEmpty());
    }

    @Test
    public void test_remove_nested(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();
//        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
//        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());


        Set<FactoryBase<?, ?>> removed = exampleFactoryA.internal().getRemoved();
        Assertions.assertEquals(2, removed.size());
        Assertions.assertTrue(removed.contains(exampleFactoryB));
        Assertions.assertTrue(removed.contains(exampleFactoryC));
    }

    @Test
    public void test_remove_nested_double_use(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();
//        ExampleFactoryC exampleFactoryC = new ExampleFactoryC();
//        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        exampleFactoryA.referenceAttribute.set(new ExampleFactoryB());


        Set<FactoryBase<?, ?>> removed = exampleFactoryA.internal().getRemoved();
        Assertions.assertEquals(0, removed.size());
    }

    @Test
    public void test_remove_list(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceListAttribute.remove(exampleFactoryB);

        Set<FactoryBase<?, ?>> removed = exampleFactoryA.internal().getRemoved();
        Assertions.assertEquals(1, removed.size());
        Assertions.assertTrue(removed.contains(exampleFactoryB));
    }

    @Test
    public void test_remove_list_set(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);
        exampleFactoryA.internal().finalise();

        exampleFactoryA.referenceListAttribute.set(List.of());

        Set<FactoryBase<?, ?>> removed = exampleFactoryA.internal().getRemoved();
        Assertions.assertEquals(1, removed.size());
        Assertions.assertTrue(removed.contains(exampleFactoryB));
    }

    @Test
    public void test_modify_value(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("111");
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();

        exampleFactoryB.stringAttribute.set("222");

        Set<FactoryBase<?, ?>> modified = exampleFactoryA.internal().getModified();
        Assertions.assertEquals(1, modified.size());
        Assertions.assertTrue(modified.contains(exampleFactoryB));
    }

    @Test
    public void test_modify_factory(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.referenceAttribute.set(new ExampleFactoryA());
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();

        exampleFactoryB.referenceAttribute.set(new ExampleFactoryA());

        Set<FactoryBase<?, ?>> modified = exampleFactoryA.internal().getModified();
        Assertions.assertEquals(1, modified.size());
        Assertions.assertTrue(modified.contains(exampleFactoryB));
    }

    @Test
    public void test_modify_factoryList(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.referenceListAttribute.add(new ExampleFactoryB());
        exampleFactoryA.internal().finalise();

        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);

        Set<FactoryBase<?, ?>> modified = exampleFactoryA.internal().getModified();
        Assertions.assertEquals(1, modified.size());
        Assertions.assertTrue(modified.contains(exampleFactoryA));
    }

    public static class ExampleFactoryD extends SimpleFactoryBase<Void,ExampleFactoryA> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");

        @Override
        protected Void createImpl() {
            return null;
        }
    }
    @Test
    public void test_attribute_root(){
        ExampleFactoryD exampleFactoryD = new ExampleFactoryD();
        exampleFactoryD.internal().finalise();
        Assertions.assertEquals(exampleFactoryD, exampleFactoryD.stringAttribute.internal_getRoot());
    }

    @Test
    public void test_attribute_root_nested(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryB.stringAttribute.set("111");
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.internal().finalise();

        Assertions.assertEquals(exampleFactoryA, exampleFactoryB.stringAttribute.internal_getRoot());
    }

    @Test
    public void test_attribute_root_root(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        Assertions.assertEquals(exampleFactoryA, exampleFactoryA.stringAttribute.internal_getRoot());
    }

    @Test
    public void test_reset(){
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("1111");
        exampleFactoryA.internal().finalise();
        exampleFactoryA.stringAttribute.set("2222");
        exampleFactoryA.internal().resetModificationFlat();
        Assertions.assertEquals("1111", exampleFactoryA.stringAttribute.get());
    }

}