package io.github.factoryfx.data.attribute.primitive.list;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleListAttributeTest {
    @Test
    public void test_json(){
        DoubleListAttribute attribute = new DoubleListAttribute();
        double value = '1';
        attribute.add(value);
        DoubleListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get(0).doubleValue(),0.00001);
    }
}