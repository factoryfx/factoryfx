package io.github.factoryfx.data.attribute.time;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class LocalDateAttributeTest {

    @Test
    public void test_json(){
        LocalDateAttribute attribute = new LocalDateAttribute();
        LocalDate value = LocalDate.now();
        attribute.set(value);
        LocalDateAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }


}