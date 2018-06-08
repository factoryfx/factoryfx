package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleAttributeTest {

    @Test
    public void test_json(){
        DoubleAttribute attribute = new DoubleAttribute();
        double value = 1;
        attribute.set(value);
        DoubleAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

    @Test
    public void test_json_2(){
        DoubleAttribute attribute = new DoubleAttribute();
        double value = 1.5342;
        attribute.set(value);
        DoubleAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().doubleValue(),0.0001);
    }

}