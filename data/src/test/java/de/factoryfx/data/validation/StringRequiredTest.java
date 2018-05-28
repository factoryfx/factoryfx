package de.factoryfx.data.validation;

import org.junit.Assert;
import org.junit.Test;

public class StringRequiredTest {

    @Test
    public void test(){
        Assert.assertFalse(new StringRequired().validate("rsfrsfdsfd").validationFailed());
        Assert.assertTrue(new StringRequired().validate("").validationFailed());
        Assert.assertTrue(new StringRequired().validate(null).validationFailed());
    }

}