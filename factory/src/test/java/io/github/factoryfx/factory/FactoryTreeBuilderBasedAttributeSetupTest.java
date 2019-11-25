package io.github.factoryfx.factory;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryViewAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.testfactories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FactoryTreeBuilderBasedAttributeSetupTest {

    @Test
    public void test_happycase_ref() {

        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            factoryBases.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factoryBases = new ExampleFactoryC();
            factoryBases.stringAttribute.set("YYY");
            return factoryBases;
        });


        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        Assertions.assertTrue(root.referenceAttribute.internal_isUserSelectable());
        Assertions.assertFalse(root.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());

        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(1, possibleValues.size());


        Assertions.assertEquals("123", possibleValues.get(0).stringAttribute.get());
        Assertions.assertEquals("YYY", possibleValues.get(0).referenceAttributeC.get().stringAttribute.get());
    }

    @Test
    public void test_happycase_list() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"1"));
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"2"));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class,"1", Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("111");
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class,"2", Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("222");
            return factoryBases;
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

//        Assertions.assertFalse(root.referenceListAttribute.internal_isUserSelectable());

        List<ExampleFactoryB> possibleValues = root.referenceListAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceListAttribute));
        Assertions.assertEquals(2, possibleValues.size());

        Assertions.assertEquals("111", possibleValues.get(0).stringAttribute.get());
        Assertions.assertEquals("222", possibleValues.get(1).stringAttribute.get());
    }



    @Test
    public void test_singleton() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            return factoryBases;
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues1 = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        List<ExampleFactoryB> possibleValues2 = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(possibleValues1.get(0),possibleValues2.get(0));
    }

    @Test
    public void test_prototype() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            return factoryBases;
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues1 = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        List<ExampleFactoryB> possibleValues2 = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertNotEquals(possibleValues1.get(0),possibleValues2.get(0));
    }


    private FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> createBuilder() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            return factoryBases;
        });
        return builder;
    }

    @Test
    public void test_singleton_after_serialisation() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = createBuilder();
        ExampleFactoryA root = builder.buildTreeUnvalidated();


        ExampleFactoryA jsonCopy = ObjectMapperBuilder.build().copy(root);
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> clientBuilder = createBuilder();//empty FactoryTreeBuilder missing the state

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(clientBuilder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(jsonCopy);

        List<ExampleFactoryB> possibleValues = jsonCopy.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(possibleValues.get(0),jsonCopy.referenceAttribute.get());
    }


    @Test
    public void test_singleton_named() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"111"));
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"222"));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            return factoryBases;
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues = root.referenceListAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceListAttribute));
        Assertions.assertEquals(2,possibleValues.size());
        Assertions.assertNotEquals(possibleValues.get(0),possibleValues.get(1));

        Assertions.assertEquals(root.referenceListAttribute.get(0),possibleValues.get(0));
        Assertions.assertEquals(root.referenceListAttribute.get(1),possibleValues.get(1));
    }

    private FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> createNamedListBuilder() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"111"));
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class,"222"));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, "111",Scope.SINGLETON, context -> {
            return new ExampleFactoryB();
        });
        builder.addFactory(ExampleFactoryB.class, "222",Scope.SINGLETON, context -> {
            return new ExampleFactoryB();
        });
        return builder;
    }

    @Test
    public void test_naed_singelton_after_serialisation() {
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = createNamedListBuilder();
        ExampleFactoryA root = builder.buildTreeUnvalidated();


        ExampleFactoryA jsonCopy = ObjectMapperBuilder.build().copy(root);
        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> clientBuilder = createNamedListBuilder();//empty FactoryTreeBuilder missing the state

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(clientBuilder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(jsonCopy);

        List<ExampleFactoryB> possibleValues = jsonCopy.referenceListAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceListAttribute));
        Assertions.assertTrue(possibleValues.get(0)==jsonCopy.referenceListAttribute.get(0));
        Assertions.assertTrue(possibleValues.get(1)==jsonCopy.referenceListAttribute.get(1));
    }


    @Test
    public void test_for_nested_added() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factoryBases = new ExampleFactoryB();
            factoryBases.stringAttribute.set("123");
            factoryBases.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            return factoryBases;
        });
        builder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factoryBases = new ExampleFactoryC();
            factoryBases.stringAttribute.set("YYY");
            return factoryBases;
        });

        ExampleFactoryA root = builder.buildTreeUnvalidated();

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);


        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(1, possibleValues.size());
        Assertions.assertEquals("123", possibleValues.get(0).stringAttribute.get());
        root.referenceAttribute.set(possibleValues.get(0));


        List<ExampleFactoryC> possibleValuesC =possibleValues.get(0).referenceAttributeC.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryB.class).getAttributeMetadata(f->f.referenceAttributeC));
        Assertions.assertEquals(1, possibleValuesC.size());
        Assertions.assertEquals("YYY", possibleValuesC.get(0).stringAttribute.get());
    }


    public static class ExampleViewFactory extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryViewRootFactory>{
        public final FactoryViewAttribute<ExampleFactoryViewRootFactory,ExampleLiveObjectB,ExamplDummyFactory> viewAttribute =new FactoryViewAttribute<>((root)->root.referenceAttribute.get());

        @Override
        protected ExampleLiveObjectB createImpl() {
            return viewAttribute.instance();
        }
    }

    public static class ExampleFactoryViewRootFactory extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryViewRootFactory>{
        public final FactoryAttribute<ExampleLiveObjectB,ExampleViewFactory> referenceAttributeViewFactory = new FactoryAttribute<>();
        public final FactoryAttribute<ExampleLiveObjectB,ExamplDummyFactory> referenceAttribute = new FactoryAttribute<>();

        @Override
        protected ExampleLiveObjectB createImpl() {
            return referenceAttribute.instance();
        }
    }

    public static class ExamplDummyFactory extends SimpleFactoryBase<ExampleLiveObjectB,ExampleFactoryViewRootFactory>{
        @Override
        protected ExampleLiveObjectB createImpl() {
            return null;
        }
    }


    @Test
    public void test_view() {

        FactoryTreeBuilder<ExampleLiveObjectB,ExampleFactoryViewRootFactory> builder = new FactoryTreeBuilder<>(ExampleFactoryViewRootFactory.class);
        builder.addFactory(ExampleViewFactory.class, Scope.PROTOTYPE);
        builder.addFactory(ExamplDummyFactory.class, Scope.PROTOTYPE);

        ExampleFactoryViewRootFactory root = builder.buildTreeUnvalidated();
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));

        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryViewRootFactory> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleViewFactory> exampleViewFactories = root.referenceAttributeViewFactory.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryViewRootFactory.class).getAttributeMetadata(f->f.referenceAttributeViewFactory));
        //no exception, no npe(root) in view function


    }

    @Test
    public void test_incomplete_builder() {

        FactoryTreeBuilder<ExampleLiveObjectA,ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
//            factoryBases.referenceAttribute.set(context.get(ExampleFactoryB.class));
            factoryBases.referenceAttribute.set(null);
            return factoryBases;
        });
        //intentional commented out
//        builder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
//            ExampleFactoryB factoryBases = new ExampleFactoryB();
//            factoryBases.stringAttribute.set("123");
//            factoryBases.referenceAttributeC.set(context.get(ExampleFactoryC.class));
//            return factoryBases;
//        });
//        builder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
//            ExampleFactoryC factoryBases = new ExampleFactoryC();
//            factoryBases.stringAttribute.set("YYY");
//            return factoryBases;
//        });


        ExampleFactoryA root = builder.buildTreeUnvalidated();
        FactoryTreeBuilderBasedAttributeSetup<ExampleFactoryA> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues(FactoryMetadataManager.getMetadata(ExampleFactoryA.class).getAttributeMetadata(f->f.referenceAttribute));
        Assertions.assertEquals(1, possibleValues.size());
    }


}