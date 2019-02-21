package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharListAttributeTest {
    @Test
    public void test_json(){
        CharListAttribute attribute = new CharListAttribute();
        char value = '1';
        attribute.add(value);
        CharListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get(0).charValue());
    }
}