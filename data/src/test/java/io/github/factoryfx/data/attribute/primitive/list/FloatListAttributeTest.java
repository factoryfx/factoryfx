package io.github.factoryfx.data.attribute.primitive.list;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatListAttributeTest {
    @Test
    public void test_json(){
        FloatListAttribute attribute = new FloatListAttribute();
        float value = 1;
        attribute.add(value);
        FloatListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get(0).floatValue(),0.00001);
    }
}