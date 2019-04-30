package io.github.factoryfx.factory.validator;

import java.util.List;

import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FactoryValidatorTest {

    @Test
    public void test() {
        final FactoryStyleValidatorBuilder factoryStyleValidatorBuilder = new FactoryStyleValidatorBuilder();
        List<FactoryStyleValidation> factoryValidations = factoryStyleValidatorBuilder.createFactoryValidations(ExampleFactoryA.class);

        for (FactoryStyleValidation factoryValidation : factoryValidations) {
            Assertions.assertEquals("",factoryValidation.validateFactory().orElse(""));
        }
    }
}
