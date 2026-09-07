package io.github.factoryfx.factory.validation;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ServerValidationTest {

    private static ValidationResult failIf(boolean error, String text) {
        return new ValidationResult(error, new LanguageText(text));
    }

    public static class ServerValidationExampleFactory extends SimpleFactoryBase<Void, ServerValidationExampleFactory> {
        public final StringAttribute stringAttribute = new StringAttribute().nullable()
                .validation(value -> failIf("clientInvalid".equals(value), "client error"))
                .serverValidation(value -> failIf("serverInvalid".equals(value), "server error"));
        public final StringAttribute requiredAttribute = new StringAttribute();//not nullable

        public ServerValidationExampleFactory() {
            config().addServerValidation((ServerValidationExampleFactory factory) ->
                    failIf("factoryLevelInvalid".equals(factory.stringAttribute.get()), "factory-level server error"), stringAttribute);
        }

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @Test
    public void test_clientValidate_excludesServerValidations() {
        ServerValidationExampleFactory factory = new ServerValidationExampleFactory().internal().finalise();
        factory.stringAttribute.set("serverInvalid");
        factory.requiredAttribute.set("set");

        Assertions.assertTrue(factory.internal().validateFlat().isEmpty());
        Assertions.assertTrue(factory.stringAttribute.internal_validate(factory, "stringAttribute").isEmpty());
    }

    @Test
    public void test_serverValidate_reportsServerValidation() {
        ServerValidationExampleFactory factory = new ServerValidationExampleFactory().internal().finalise();
        factory.stringAttribute.set("serverInvalid");
        factory.requiredAttribute.set("set");

        List<ValidationError> errors = factory.internal().validateFlatServer();
        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.get(0).getSimpleErrorDescription().contains("server error"), errors.get(0).getSimpleErrorDescription());
    }

    @Test
    public void test_serverValidate_excludesClientValidations_andRequired() {
        ServerValidationExampleFactory factory = new ServerValidationExampleFactory().internal().finalise();
        factory.stringAttribute.set("clientInvalid");
        //requiredAttribute stays null: the required check is a client validation

        Assertions.assertTrue(factory.internal().validateFlatServer().isEmpty());
        Assertions.assertFalse(factory.internal().validateFlat().isEmpty());
    }

    @Test
    public void test_serverValidate_factoryLevel() {
        ServerValidationExampleFactory factory = new ServerValidationExampleFactory().internal().finalise();
        factory.stringAttribute.set("factoryLevelInvalid");
        factory.requiredAttribute.set("set");

        List<ValidationError> errors = factory.internal().validateFlatServer();
        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.get(0).getSimpleErrorDescription().contains("factory-level server error"), errors.get(0).getSimpleErrorDescription());
        Assertions.assertTrue(factory.internal().validateFlat().isEmpty());
    }

    @Test
    public void test_addServerValidation_withoutDependencies_throws() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SimpleFactoryBase<Void, ServerValidationExampleFactory>() {
            {
                config().addServerValidation((factory) -> ValidationResult.OK);
            }

            @Override
            protected Void createImpl() {
                return null;
            }
        });
    }
}
