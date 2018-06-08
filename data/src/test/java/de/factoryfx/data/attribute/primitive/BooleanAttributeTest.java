package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanAttributeTest {

    @Test
    public void test_json(){
        BooleanAttribute booleanAttribute = new BooleanAttribute();
        booleanAttribute.set(true);
        BooleanAttribute copy = ObjectMapperBuilder.build().copy(booleanAttribute);
        Assert.assertEquals(true,copy.get());
    }

}