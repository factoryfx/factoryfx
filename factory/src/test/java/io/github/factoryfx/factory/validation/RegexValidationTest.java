package io.github.factoryfx.factory.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexValidationTest {

    @Test
    public void test_RegexValidation() {
        RegexValidation validation = new RegexValidation(Pattern.compile("[0-9]*"));
        Assertions.assertTrue(validation.validate("sfdsfdsfd").validationFailed());
        Assertions.assertFalse(validation.validate("1234").validationFailed());
        Assertions.assertFalse(validation.validate(null).validationFailed());
    }

}