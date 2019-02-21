package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongListAttributeTest {

    @Test
    public void test_json(){
        LongListAttribute attribute = new LongListAttribute();
        long value = 1;
        attribute.add(value);
        LongListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(1,copy.get(0).longValue());
    }

}