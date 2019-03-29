package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatAttributeTest {

    @Test
    public void test_json(){
        FloatAttribute attribute = new FloatAttribute();
        float value = 1;
        attribute.set(value);
        FloatAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

    @Test
    public void test_json_2(){
        FloatAttribute attribute = new FloatAttribute();
        float value = 1.532f;
        attribute.set(value);
        FloatAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

}