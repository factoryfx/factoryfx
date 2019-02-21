package de.factoryfx.data.attribute.time;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class LocalDateTimeAttributeTest {
    @Test
    public void test_json(){
        LocalDateTimeAttribute attribute = new LocalDateTimeAttribute();
        LocalDateTime value = LocalDateTime.now();
        attribute.set(value);
        LocalDateTimeAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }

}