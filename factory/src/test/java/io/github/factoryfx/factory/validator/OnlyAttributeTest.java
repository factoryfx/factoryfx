package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class OnlyAttributeTest {
    private static class OnlyAttributeFactory extends FactoryBase<Void, OnlyAttributeFactory> {
        public String blub;
    }

    @Test
    public void test() throws NoSuchFieldException {
        Optional<String> result = new OnlyAttribute(OnlyAttributeFactory.class, OnlyAttributeFactory.class.getField("blub")).validateFactory();
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void test_negative() throws NoSuchFieldException {
        Optional<String> result = new OnlyAttribute(ExampleFactoryA.class,ExampleFactoryA.class.getField("stringAttribute")).validateFactory();
        Assertions.assertFalse(result.isPresent());
    }
}