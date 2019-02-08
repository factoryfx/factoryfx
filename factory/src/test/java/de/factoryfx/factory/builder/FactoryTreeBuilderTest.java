package de.factoryfx.factory.builder;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.testfactories.*;
import de.factoryfx.factory.testfactories.poly.ErrorPrinter;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FactoryTreeBuilderTest {

    @Test
    public void test_simple(){
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(de.factoryfx.factory.testfactories.ExampleFactoryA.class);

        factoryTreeBuilder.addFactory(ExampleFactoryA.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(de.factoryfx.factory.testfactories.ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(de.factoryfx.factory.testfactories.ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(de.factoryfx.factory.testfactories.ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            de.factoryfx.factory.testfactories.ExampleFactoryB factory = new de.factoryfx.factory.testfactories.ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(de.factoryfx.factory.testfactories.ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(de.factoryfx.factory.testfactories.ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            de.factoryfx.factory.testfactories.ExampleFactoryC factory = new de.factoryfx.factory.testfactories.ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });


        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();


        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test
    public void test_simple_defaultcreator(){
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class);

        factoryTreeBuilder.addFactory(de.factoryfx.factory.testfactories.ExampleFactoryA.class, Scope.PROTOTYPE);
        factoryTreeBuilder.addFactory(de.factoryfx.factory.testfactories.ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            de.factoryfx.factory.testfactories.ExampleFactoryB factory = new de.factoryfx.factory.testfactories.ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(de.factoryfx.factory.testfactories.ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(de.factoryfx.factory.testfactories.ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            de.factoryfx.factory.testfactories.ExampleFactoryC factory = new de.factoryfx.factory.testfactories.ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });

        ExampleFactoryA root = factoryTreeBuilder.buildTreeUnvalidated();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    public static class FactoryTestA extends SimpleFactoryBase<Void,Void,FactoryTestA> {

        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute1 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute2 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");
        public final FactoryReferenceListAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceList = new FactoryReferenceListAttribute<>(ExampleFactoryB.class).labelText("ExampleA3");


        @Override
        public Void createImpl() {
            return null;
        }
    }

    public static class ExampleFactoryC extends SimpleFactoryBase<ExampleLiveObjectC,Void,FactoryTestA> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
        public final FactoryReferenceAttribute<ExampleLiveObjectB, de.factoryfx.factory.testfactories.ExampleFactoryB> referenceAttribute = new FactoryReferenceAttribute<>(de.factoryfx.factory.testfactories.ExampleFactoryB.class).labelText("ExampleA2");

        @Override
        public ExampleLiveObjectC createImpl() {
            return new ExampleLiveObjectC();
        }

    }

    public static class ExampleFactoryB extends SimpleFactoryBase<ExampleLiveObjectB,Void,FactoryTestA> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleB1");
        public final FactoryReferenceAttribute<Void,FactoryTestA> referenceAttribute = new FactoryReferenceAttribute<>(FactoryTestA.class).labelText("ExampleB2");
        public final FactoryReferenceAttribute<ExampleLiveObjectC,ExampleFactoryC> referenceAttributeC = new FactoryReferenceAttribute<>(ExampleFactoryC.class).labelText("ExampleC2");

        @Override
        public ExampleLiveObjectB createImpl() {
            return new ExampleLiveObjectB(referenceAttributeC.instance());
        }

    }


    @Test
    public void test_singelton(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
//            factory.referenceAttributeC.set(context.get(FactoryTestA.class));
            return factory;
        });

        FactoryTestA root = factoryTreeBuilder.buildTreeUnvalidated();
        Assert.assertEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());
    }

    @Test
    public void test_prototype(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            return factory;
        });

        FactoryTestA root = factoryTreeBuilder.buildTreeUnvalidated();
        Assert.assertNotEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());

    }

    private static class ErrorPrinterFactory2 extends PolymorphicFactoryBase<Printer,Void,ExamplePolymorphic> {
        @Override
        public Printer createImpl() {
            return new ErrorPrinter();
        }

    }

    public static class ExamplePolymorphic extends FactoryBase<Void,Void,ExamplePolymorphic>{
        public final FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class, ErrorPrinterFactory2.class, OutPrinterFactory.class);
    }


    @Test
    public void test_polymorphic(){
        FactoryTreeBuilder<Void,Void,ExamplePolymorphic,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ExamplePolymorphic.class);

        factoryTreeBuilder.addFactory(ExamplePolymorphic.class, Scope.SINGLETON);

        factoryTreeBuilder.addFactory(ErrorPrinterFactory2.class, Scope.SINGLETON);

        ExamplePolymorphic root = factoryTreeBuilder.buildTreeUnvalidated();
        Assert.assertNotNull(root.attribute.get());

    }


    @Test(expected=IllegalStateException.class)
    public void test_simple_validation(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE);
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
            factory.referenceAttribute.set(new de.factoryfx.factory.testfactories.ExampleFactoryB());
            return factory;
        });

        FactoryTestA root = factoryTreeBuilder.buildTree();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test(expected=IllegalStateException.class)
    public void test_root_creator_missing(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        //intentional commented out(test docu):  factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE);
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
            factory.referenceAttribute.set(new de.factoryfx.factory.testfactories.ExampleFactoryB());
            return factory;
        });

        factoryTreeBuilder.buildTreeUnvalidated();
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_root_mandatory(){
        new FactoryTreeBuilder<>(null);
    }

    @Test
    public void test_singleton_named(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);
        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceList.add(context.get(ExampleFactoryB.class,"111"));
            factory.referenceList.add(context.get(ExampleFactoryB.class,"222"));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        FactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assert.assertTrue(factoryTestA.referenceList.get(0)!=factoryTestA.referenceList.get(1));
    }

    @Test
    public void test_singleton_named_getList(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);
        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceList.addAll(context.getList(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(null);
            factory.referenceAttribute.set(new FactoryTestA());
            return factory;
        });
        FactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assert.assertTrue(factoryTestA.referenceList.get(0)!=factoryTestA.referenceList.get(1));
    }

    @Test
    public void test_mutiple_named_getList(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = createBuilder();
        FactoryTestA factoryTestA = factoryTreeBuilder.buildTreeUnvalidated();

        Assert.assertTrue(factoryTestA.referenceList.get(0)==factoryTestA.referenceList.get(2));
        Assert.assertTrue(factoryTestA.referenceList.get(1)==factoryTestA.referenceList.get(3));

        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder2 = createBuilder();
        factoryTreeBuilder2.fillFromExistingFactoryTree(factoryTestA);
        FactoryTestA factoryTestA2 = factoryTreeBuilder.buildTreeUnvalidated();

        Assert.assertTrue(factoryTestA2.referenceList.get(0)==factoryTestA2.referenceList.get(2));
        Assert.assertTrue(factoryTestA2.referenceList.get(1)==factoryTestA2.referenceList.get(3));
    }

    private FactoryTreeBuilder<Void,Void,FactoryTestA,Void> createBuilder() {
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);
        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceList.addAll(context.getList(ExampleFactoryB.class));
            factory.referenceList.addAll(context.getList(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"111", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class,"222", Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            return factory;
        });
        return factoryTreeBuilder;
    }


    @Test(expected = IllegalStateException.class)
    public void test_incomplete_builder_class() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(de.factoryfx.factory.testfactories.ExampleFactoryB.class));
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



        ExampleFactoryA root = builder.buildTreeUnvalidated();
    }

    @Test(expected = IllegalStateException.class)
    public void test_incomplete_builder_classAndName() {

        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA,Void> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class);
        builder.addFactory(ExampleFactoryA.class, Scope.SINGLETON, context -> {
            ExampleFactoryA factoryBases = new ExampleFactoryA();
            factoryBases.referenceAttribute.set(context.get(de.factoryfx.factory.testfactories.ExampleFactoryB.class,"dfgdgf"));
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



        ExampleFactoryA root = builder.buildTreeUnvalidated();
    }

    @Test
    public void test_dataDictionary(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> builder = new FactoryTreeBuilder<>(FactoryTestA.class);
        builder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            return new ExampleFactoryB();
        });

        DataStorageMetadataDictionary dataStorageMetadataDictionary = builder.buildTreeUnvalidated().internal().createDataStorageMetadataDictionaryFromRoot();
        Assert.assertTrue(dataStorageMetadataDictionary.containsClass(FactoryTestA.class.getName()));
        Assert.assertTrue(dataStorageMetadataDictionary.containsClass(ExampleFactoryB.class.getName()));
    }

    public static class ExampleFactoryB2 extends ExampleFactoryB {
    }

    @Test
    public void test_dataDictionary_inheritance(){
        FactoryTreeBuilder<Void,Void,FactoryTestA,Void> builder = new FactoryTreeBuilder<>(FactoryTestA.class);
        builder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            return new ExampleFactoryB2();
        });

        DataStorageMetadataDictionary dataStorageMetadataDictionary = builder.buildTreeUnvalidated().internal().createDataStorageMetadataDictionaryFromRoot();
        Assert.assertTrue(dataStorageMetadataDictionary.containsClass(FactoryTestA.class.getName()));
        Assert.assertTrue(dataStorageMetadataDictionary.containsClass(ExampleFactoryB2.class.getName()));
    }
}