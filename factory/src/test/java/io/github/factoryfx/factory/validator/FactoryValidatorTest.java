package io.github.factoryfx.factory.validator;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;

public class FactoryValidatorTest {

    @Test
    public void test() {
        new FactoryStyleValidatorBuilder().createFactoryValidations(ExampleFactoryA.class)
                                          .stream()
                                          .map(FactoryStyleValidation::validateFactory)
                                          .filter(Optional::isPresent)
                                          .map(Optional::get)
                                          .forEach(Assertions::assertNull);
    }

    // Factory Validator Test

    public class ExampleFactoryFail1 extends FactoryBase<String, ExampleFactoryFail1> {
        public final StringAttribute stringAttribute = new StringAttribute();
    }

    public static class ExampleFactoryFail2 extends FactoryBase<String, ExampleFactoryFail1> {
        public final StringAttribute stringAttribute = null;
    }

    @Test
    public void testFail() {
        Assertions.assertEquals("class " + ExampleFactoryFail1.class.getName() + " is an inner class, which must not be a Factory class (Non-static nested classes are called inner classes)",
                                new FactoryStyleValidatorBuilder().createFactoryValidations(ExampleFactoryFail1.class)
                                                                  .stream()
                                                                  .map(FactoryStyleValidation::validateFactory)
                                                                  .filter(Optional::isPresent)
                                                                  .map(Optional::get)
                                                                  .findAny()
                                                                  .get());
    }

    //@Test
    public void testFail2() {
        Assertions.assertEquals("Must not be null: " + ExampleFactoryFail2.class.getName() + "#stringAttribute",
                                new FactoryStyleValidatorBuilder().createFactoryValidations(ExampleFactoryFail2.class)
                                                                  .stream()
                                                                  .map(FactoryStyleValidation::validateFactory)
                                                                  .filter(Optional::isPresent)
                                                                  .map(Optional::get)
                                                                  .findAny()
                                                                  .get());
    }

}
