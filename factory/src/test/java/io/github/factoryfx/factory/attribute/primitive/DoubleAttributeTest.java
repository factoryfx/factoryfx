package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleAttributeTest {

    @Test
    public void test_json(){
        DoubleAttribute attribute = new DoubleAttribute();
        double value = 1;
        attribute.set(value);
        DoubleAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

    @Test
    public void test_json_2(){
        DoubleAttribute attribute = new DoubleAttribute();
        double value = 1.5342;
        attribute.set(value);
        DoubleAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

}