package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortAttributeTest {


    @Test
    public void test_json(){
        ShortAttribute attribute = new ShortAttribute();
        short value = 1;
        attribute.set(value);
        ShortAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().shortValue());
    }


}