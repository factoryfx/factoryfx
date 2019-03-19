package io.github.factoryfx.data.attribute.primitive.list;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortListAttributeTest {

    @Test
    public void test_json(){
        ShortListAttribute attribute = new ShortListAttribute();
        short value = 1;
        attribute.add(value);
        ShortListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(1,copy.get(0).shortValue());
    }

}