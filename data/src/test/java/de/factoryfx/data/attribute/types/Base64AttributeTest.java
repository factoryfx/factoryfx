package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class Base64AttributeTest {

    @Test
    public void test_json(){
        Base64Attribute attribute= new Base64Attribute();
        attribute.set(new byte[1]);
        Base64Attribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertArrayEquals(new byte[1],copy.getBytes());
    }

}