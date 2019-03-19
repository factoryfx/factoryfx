package io.github.factoryfx.data.attribute.primitive;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteAttributeTest {

    @Test
    public void test_json(){
        ByteAttribute attribute = new ByteAttribute();
        byte value = 10;
        attribute.set(value);
        ByteAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().byteValue());
    }


}