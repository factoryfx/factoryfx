package io.github.factoryfx.factory.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;

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

    @Test
    public void test_dublicate(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(ExampleFactoryA.class, null, Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(ExampleFactoryA.class, null, Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertTrue(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_dublicate2(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(ExampleFactoryA.class, "123", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(ExampleFactoryA.class, "123", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertTrue(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_dublicate3(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(ExampleFactoryA.class, "", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(ExampleFactoryA.class, "123", Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertFalse(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_dublicate4(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(ExampleFactoryA.class,null, Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryB,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(ExampleFactoryB.class,null , Scope.PROTOTYPE, context -> {
            return new ExampleFactoryB();
        });
        Assertions.assertFalse(factoryCreator1.isDuplicate(factoryCreator2));
    }

}