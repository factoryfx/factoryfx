package de.factoryfx.factory.validation;

import org.junit.Assert;
import org.junit.Test;

public class StringRequiredTest {

    @Test
    public void test(){
        Assert.assertTrue(new StringRequired().validate("rsfrsfdsfd"));
        Assert.assertFalse(new StringRequired().validate(""));
        Assert.assertFalse(new StringRequired().validate(null));
    }

}