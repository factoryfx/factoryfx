package io.github.factoryfx.factory.attribute.time;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class LocalTimeAttributeTest {

    @Test
    public void test_json(){
        LocalTimeAttribute attribute = new LocalTimeAttribute();
        LocalTime value = LocalTime.now();
        attribute.set(value);
        LocalTimeAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }


}