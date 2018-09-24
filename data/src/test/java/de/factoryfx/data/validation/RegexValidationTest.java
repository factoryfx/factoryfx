package de.factoryfx.data.validation;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class RegexValidationTest {

    @Test
    public void test_RegexValidation() {
        RegexValidation validation = new RegexValidation(Pattern.compile("[0-9]*"));
        Assert.assertTrue(validation.validate("sfdsfdsfd").validationFailed());
        Assert.assertFalse(validation.validate("1234").validationFailed());
        Assert.assertFalse(validation.validate(null).validationFailed());
    }

}