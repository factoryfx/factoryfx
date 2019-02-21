package de.factoryfx.factory;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.factory.testfactories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FactoryTreeBuilderBasedAttributeSetupTest {

    @Test
    public void test_happycase_ref() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        Assertions.assertTrue(root.referenceAttribute.internal_isUserSelectable());
        Assertions.assertFalse(root.referenceAttribute.get().referenceAttribute.internal_isUserSelectable());

        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(1, possibleValues.size());


        Assertions.assertEquals("123", possibleValues.get(0).stringAttribute.get());
        Assertions.assertEquals("YYY", possibleValues.get(0).referenceAttributeC.get().stringAttribute.get());
    }

    @Test
    public void test_happycase_list() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceListAttribute.add(context.get(ExampleFactoryB.class));
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

//        Assertions.assertFalse(root.referenceListAttribute.internal_isUserSelectable());

        List<ExampleFactoryB> possibleValues = root.referenceListAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(2, possibleValues.size());

        Assertions.assertEquals("111", possibleValues.get(0).stringAttribute.get());
        Assertions.assertEquals("222", possibleValues.get(1).stringAttribute.get());
    }



    @Test
    public void test_singleton() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues1 = root.referenceAttribute.internal_createNewPossibleValues();
        List<ExampleFactoryB> possibleValues2 = root.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(possibleValues1.get(0),possibleValues2.get(0));
    }

    @Test
    public void test_prototype() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues1 = root.referenceAttribute.internal_createNewPossibleValues();
        List<ExampleFactoryB> possibleValues2 = root.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertNotEquals(possibleValues1.get(0),possibleValues2.get(0));
    }


    private FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> createBuilder() {
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = createBuilder();
        ExampleFactoryA root = builder.buildTreeUnvalidated();


        ExampleFactoryA jsonCopy = ObjectMapperBuilder.build().copy(root);
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> clientBuilder = createBuilder();//empty FactoryTreeBuilder missing the state

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(clientBuilder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(jsonCopy);

        List<ExampleFactoryB> possibleValues = jsonCopy.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(possibleValues.get(0),jsonCopy.referenceAttribute.get());
    }


    @Test
    public void test_singleton_named() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceListAttribute.addAll(context.getList(ExampleFactoryB.class));
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues = root.referenceListAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(2,possibleValues.size());
        Assertions.assertNotEquals(possibleValues.get(0),possibleValues.get(1));

        Assertions.assertEquals(root.referenceListAttribute.get(0),possibleValues.get(0));
        Assertions.assertEquals(root.referenceListAttribute.get(1),possibleValues.get(1));
    }

    private FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> createNamedListBuilder() {
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = createNamedListBuilder();
        ExampleFactoryA root = builder.buildTreeUnvalidated();


        ExampleFactoryA jsonCopy = ObjectMapperBuilder.build().copy(root);
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> clientBuilder = createNamedListBuilder();//empty FactoryTreeBuilder missing the state

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(clientBuilder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(jsonCopy);

        List<ExampleFactoryB> possibleValues = jsonCopy.referenceListAttribute.internal_createNewPossibleValues();
        Assertions.assertTrue(possibleValues.get(0)==jsonCopy.referenceListAttribute.get(0));
        Assertions.assertTrue(possibleValues.get(1)==jsonCopy.referenceListAttribute.get(1));
    }


    @Test
    public void test_for_added() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);


        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(1, possibleValues.size());
        Assertions.assertEquals("123", possibleValues.get(0).stringAttribute.get());



        List<ExampleFactoryC> possibleValuesC =possibleValues.get(0).referenceAttributeC.internal_createNewPossibleValues();
        Assertions.assertEquals(1, possibleValuesC.size());
        Assertions.assertEquals("YYY", possibleValuesC.get(0).stringAttribute.get());
    }


    public static class ExampleViewFactory extends SimpleFactoryBase<ExampleLiveObjectB,Void,ExampleFactoryViewRootFactory>{
        public final FactoryViewReferenceAttribute<ExampleFactoryViewRootFactory,ExampleLiveObjectB,ExamplDummyFactory> viewAttribute =new FactoryViewReferenceAttribute<>((root)->root.referenceAttribute.get());

        @Override
        public ExampleLiveObjectB createImpl() {
            return viewAttribute.instance();
        }
    }

    public static class ExampleFactoryViewRootFactory extends SimpleFactoryBase<ExampleLiveObjectB,Void,ExampleFactoryViewRootFactory>{
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleViewFactory> referenceAttributeViewFactory = new FactoryReferenceAttribute<>(ExampleViewFactory.class);
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExamplDummyFactory> referenceAttribute = new FactoryReferenceAttribute<>(ExamplDummyFactory.class);

        @Override
        public ExampleLiveObjectB createImpl() {
            return referenceAttribute.instance();
        }
    }

    public static class ExamplDummyFactory extends SimpleFactoryBase<ExampleLiveObjectB,Void,ExampleFactoryViewRootFactory>{
        @Override
        public ExampleLiveObjectB createImpl() {
            return null;
        }
    }


    @Test
    public void test_view() {

        FactoryTreeBuilder<Void,ExampleLiveObjectB,ExampleFactoryViewRootFactory,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryViewRootFactory.class);
        builder.addFactory(ExampleFactoryViewRootFactory.class, Scope.SINGLETON);
        builder.addFactory(ExampleViewFactory.class, Scope.PROTOTYPE);
        builder.addFactory(ExamplDummyFactory.class, Scope.PROTOTYPE);

        ExampleFactoryViewRootFactory root = builder.buildTreeUnvalidated();
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));

        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectB,ExampleFactoryViewRootFactory,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleViewFactory> exampleViewFactories = root.referenceAttributeViewFactory.internal_createNewPossibleValues();
        //no exception, no npe(root) in view function


    }

    @Test
    public void test_incomplete_builder() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
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
        FactoryTreeBuilderBasedAttributeSetup<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilderBasedAttributeSetup = new FactoryTreeBuilderBasedAttributeSetup<>(builder);
        factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);

        List<ExampleFactoryB> possibleValues = root.referenceAttribute.internal_createNewPossibleValues();
        Assertions.assertEquals(1, possibleValues.size());
    }


}