package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ByteListAttributeTest {
    @Test
    public void test_json(){
        ByteListAttribute attribute = new ByteListAttribute();
        byte value = 1;
        attribute.add(value);
        ByteListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get(0).byteValue());
    }
}