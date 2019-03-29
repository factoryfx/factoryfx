package io.github.factoryfx.factory.attribute.primitive;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BooleanAttributeTest {

    @Test
    public void test_json(){
        BooleanAttribute booleanAttribute = new BooleanAttribute();
        booleanAttribute.set(true);
        BooleanAttribute copy = ObjectMapperBuilder.build().copy(booleanAttribute);
        Assertions.assertEquals(true,copy.get());
    }

}