package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FinalValidationTest {
    private static class NotFinalFactory extends FactoryBase<Void, NotFinalFactory> {
        public StringAttribute stringAttribute = new StringAttribute();
    }

    @Test
    public void test() throws NoSuchFieldException {
        Optional<String> result = new FinalValidation(NotFinalFactory.class,NotFinalFactory.class.getField("stringAttribute")).validateFactory();
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void test_negative() throws NoSuchFieldException {
        Optional<String> result = new FinalValidation(ExampleFactoryA.class,ExampleFactoryA.class.getField("stringAttribute")).validateFactory();
        Assertions.assertFalse(result.isPresent());
    }
}