package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongAttributeTest {

    @Test
    public void test_json(){
        LongAttribute attribute = new LongAttribute();
        long value = 1;
        attribute.set(value);
        LongAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().longValue());
    }


}