package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import org.junit.Assert;
import org.junit.Test;

public class StringAttributeTest {

    @Test
    public void test_equals(){
        StringAttribute stringAttribute1 = new StringAttribute(new AttributeMetadata());
        stringAttribute1.set("1234");

        StringAttribute stringAttribute2 = new StringAttribute(new AttributeMetadata());
        stringAttribute2.set("1234");

        Assert.assertTrue(stringAttribute1.equals(stringAttribute2));
        Assert.assertTrue(stringAttribute2.equals(stringAttribute1));

        StringAttribute stringAttribute3 = new StringAttribute(new AttributeMetadata());
        stringAttribute3.set("1234%");
        Assert.assertFalse(stringAttribute1.equals(stringAttribute3));
    }

}