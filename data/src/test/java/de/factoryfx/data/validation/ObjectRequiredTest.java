package de.factoryfx.data.validation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ObjectRequiredTest {

    @Test
    public void test_list(){
        Assert.assertTrue(new ObjectRequired<List<String>>().validate(new ArrayList<>()).validationFailed());
        final ArrayList<String> values = new ArrayList<>();
        values.add("1213");
        Assert.assertFalse(new ObjectRequired<List<String>>().validate(values).validationFailed());
    }

}