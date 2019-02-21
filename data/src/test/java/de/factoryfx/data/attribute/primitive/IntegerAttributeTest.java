package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerAttributeTest {

    @Test
    public void test_json(){
        IntegerAttribute attribute = new IntegerAttribute();
        int value = 1;
        attribute.set(value);
        IntegerAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().intValue());
    }

}