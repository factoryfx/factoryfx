package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class FloatAttributeTest {

    @Test
    public void test_json(){
        FloatAttribute attribute = new FloatAttribute();
        float value = 1;
        attribute.set(value);
        FloatAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

    @Test
    public void test_json_2(){
        FloatAttribute attribute = new FloatAttribute();
        float value = 1.532f;
        attribute.set(value);
        FloatAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

}