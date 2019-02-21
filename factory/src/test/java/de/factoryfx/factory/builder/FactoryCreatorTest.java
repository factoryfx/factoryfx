package de.factoryfx.factory.builder;

import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryCreatorTest {

    @Test
    public void test_singleton_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(ExampleFactoryA.class, "", Scope.SINGLETON, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assertions.assertEquals(first,second);
    }

    @Test
    public void test_prototype_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(ExampleFactoryA.class, "", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assertions.assertNotEquals(first,second);
    }

}