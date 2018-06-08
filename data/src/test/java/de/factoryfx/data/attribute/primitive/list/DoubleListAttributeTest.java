package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleListAttributeTest {
    @Test
    public void test_json(){
        DoubleListAttribute attribute = new DoubleListAttribute();
        double value = '1';
        attribute.add(value);
        DoubleListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get(0).doubleValue(),0.00001);
    }
}