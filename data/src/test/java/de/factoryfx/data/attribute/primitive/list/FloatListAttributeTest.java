package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FloatListAttributeTest {
    @Test
    public void test_json(){
        FloatListAttribute attribute = new FloatListAttribute();
        float value = 1;
        attribute.add(value);
        FloatListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get(0).floatValue(),0.00001);
    }
}