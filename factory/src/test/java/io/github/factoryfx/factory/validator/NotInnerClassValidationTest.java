package io.github.factoryfx.factory.validator;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class NotInnerClassValidationTest {

    private class InnerFactory extends FactoryBase<Void,InnerFactory> {
        public final  StringAttribute stringAttribute = new StringAttribute();
    }

    @Test
    public void test(){
        Optional<String> result = new NotInnerClassValidation(InnerFactory.class).validateFactory();
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void test_negative(){
        Optional<String> result = new NotInnerClassValidation(ExampleFactoryA.class).validateFactory();
        Assertions.assertFalse(result.isPresent());
    }
}