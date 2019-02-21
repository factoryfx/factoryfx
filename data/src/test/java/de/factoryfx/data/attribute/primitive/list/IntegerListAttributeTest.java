package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerListAttributeTest {

    @Test
    public void test_json(){
        IntegerListAttribute attribute = new IntegerListAttribute();
        int value = 1;
        attribute.add(value);
        IntegerListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(1,copy.get(0).intValue());
    }
}