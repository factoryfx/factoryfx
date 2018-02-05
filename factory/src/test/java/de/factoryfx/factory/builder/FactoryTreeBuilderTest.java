package de.factoryfx.factory.builder;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.testfactories.*;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;
import org.junit.Assert;
import org.junit.Test;

public class FactoryTreeBuilderTest {

    @Test
    public void test_simple(){
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class);

        factoryTreeBuilder.addFactory(ExampleFactoryA.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });


        ExampleFactoryA root = factoryTreeBuilder.buildTree();


        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test
    public void test_simple_defaultcreator(){
        FactoryTreeBuilder<Void,ExampleLiveObjectA,ExampleFactoryA> factoryTreeBuilder = new FactoryTreeBuilder<>(ExampleFactoryA.class);

        factoryTreeBuilder.addFactory(ExampleFactoryA.class, Scope.PROTOTYPE);
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
//            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
//            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });

        ExampleFactoryA root = factoryTreeBuilder.buildTree();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    public static class FactoryTestA extends SimpleFactoryBase<Void,Void> {

        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute1 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute2 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_singelton(){
        FactoryTreeBuilder<Void,Void,FactoryTestA> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });

        FactoryTestA root = factoryTreeBuilder.buildTree();
        Assert.assertEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());
    }

    @Test
    public void test_prototype(){
        FactoryTreeBuilder<Void,Void,FactoryTestA> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);

        factoryTreeBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryTreeBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });

        FactoryTestA root = factoryTreeBuilder.buildTree();
        Assert.assertNotEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());

    }


    public static class ExamplePolymorphic extends FactoryBase<Void,Void>{
        public final FactoryPolymorphicReferenceAttribute<Printer> attribute = new FactoryPolymorphicReferenceAttribute<>(Printer.class, ErrorPrinterFactory.class, OutPrinterFactory.class);
    }

    @Test
    public void test_polymorphic(){
        FactoryTreeBuilder<Void,Void,ExamplePolymorphic> factoryTreeBuilder = new FactoryTreeBuilder<>(ExamplePolymorphic.class);

        factoryTreeBuilder.addFactory(ExamplePolymorphic.class, Scope.SINGLETON);

        factoryTreeBuilder.addFactory(ErrorPrinterFactory.class, Scope.SINGLETON);

        ExamplePolymorphic root = factoryTreeBuilder.buildTree();
        Assert.assertNotNull(root.attribute.get());

    }


}