package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerListAttributeTest {

    @Test
    public void test_json(){
        IntegerListAttribute attribute = new IntegerListAttribute();
        int value = 1;
        attribute.add(value);
        IntegerListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(1,copy.get(0).intValue());
    }
}