package de.factoryfx.factory.builder;

import de.factoryfx.data.attribute.DefaultNewValueProvider;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryCreatorTest {

    @Test
    public void test_singleton_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(ExampleFactoryA.class, "", Scope.SINGLETON, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assert.assertEquals(first,second);
    }

    @Test
    public void test_prototype_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(ExampleFactoryA.class, "", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assert.assertNotEquals(first,second);
    }

}