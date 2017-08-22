package de.factoryfx.factory.builder;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.testfactories.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

public class FactoryBuilderTest {

    @Test
    public void test_simple(){
        FactoryBuilder<Void,ExampleLiveObjectA,ExampleFactoryA> factoryBuilder = new FactoryBuilder<>(ExampleFactoryA.class);

        factoryBuilder.addFactory(ExampleFactoryA.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.set(context.getList(ExampleFactoryB.class));
            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });
        factoryBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryC factory = new ExampleFactoryC();
            factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            return factory;
        });


        ExampleFactoryA root = factoryBuilder.build();


        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    @Test
    public void test_simple_defaultcreator(){
        FactoryBuilder<Void,ExampleLiveObjectA,ExampleFactoryA> factoryBuilder = new FactoryBuilder<>(ExampleFactoryA.class);

        factoryBuilder.addFactory(ExampleFactoryA.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(ExampleFactoryC.class, Scope.PROTOTYPE);

        ExampleFactoryA root = factoryBuilder.build();

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));
    }

    private static class FactoryTestA extends SimpleFactoryBase<Void,Void> {

        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute1 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");
        public final FactoryReferenceAttribute<ExampleLiveObjectB,ExampleFactoryB> referenceAttribute2 = new FactoryReferenceAttribute<>(ExampleFactoryB.class).labelText("ExampleA2");

        @Override
        public Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_singelton(){
        FactoryBuilder<Void,Void,FactoryTestA> factoryBuilder = new FactoryBuilder<>(FactoryTestA.class);

        factoryBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryBuilder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });

        FactoryTestA root = factoryBuilder.build();
        Assert.assertEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());
    }

    @Test
    public void test_prototype(){
        FactoryBuilder<Void,Void,FactoryTestA> factoryBuilder = new FactoryBuilder<>(FactoryTestA.class);

        factoryBuilder.addFactory(FactoryTestA.class, Scope.PROTOTYPE, context -> {
            FactoryTestA factory = new FactoryTestA();
            factory.referenceAttribute1.set(context.get(ExampleFactoryB.class));
            factory.referenceAttribute2.set(context.get(ExampleFactoryB.class));
            return factory;
        });
        factoryBuilder.addFactory(ExampleFactoryB.class, Scope.PROTOTYPE, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttribute.set(context.get(ExampleFactoryA.class));
            return factory;
        });

        FactoryTestA root = factoryBuilder.build();
        Assert.assertNotEquals(root.referenceAttribute1.get(),root.referenceAttribute2.get());

    }

}