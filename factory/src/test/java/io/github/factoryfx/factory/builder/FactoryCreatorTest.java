package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;

public class FactoryCreatorTest {

    @Test
    public void test_singleton_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, ""), Scope.SINGLETON, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assertions.assertEquals(first,second);
    }

    @Test
    public void test_prototype_scope(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, ""), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        ExampleFactoryA first = factoryCreator.create(null);
        ExampleFactoryA second = factoryCreator.create(null);

        Assertions.assertNotEquals(first,second);
    }

    @Test
    public void test_duplicate(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, null), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, null), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertTrue(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_duplicate2(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, "123"), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, "123"), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertTrue(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_dduplicate3(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, ""), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class, "123"), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });
        Assertions.assertFalse(factoryCreator1.isDuplicate(factoryCreator2));
    }

    @Test
    public void test_duplicate4(){
        FactoryCreator<ExampleFactoryA,ExampleFactoryA> factoryCreator1 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryA.class,null), Scope.PROTOTYPE, context -> {
            return new ExampleFactoryA();
        });

        FactoryCreator<ExampleFactoryB,ExampleFactoryA> factoryCreator2 = new FactoryCreator<>(new FactoryTemplateId<>(ExampleFactoryB.class,null) , Scope.PROTOTYPE, context -> {
            return new ExampleFactoryB();
        });
        Assertions.assertFalse(factoryCreator1.isDuplicate(factoryCreator2));
    }

}