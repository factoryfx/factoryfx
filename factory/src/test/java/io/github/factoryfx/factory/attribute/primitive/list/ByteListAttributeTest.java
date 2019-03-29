package io.github.factoryfx.factory.attribute.primitive.list;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteListAttributeTest {
    @Test
    public void test_json(){
        ByteListAttribute attribute = new ByteListAttribute();
        byte value = 1;
        attribute.add(value);
        ByteListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get(0).byteValue());
    }
}