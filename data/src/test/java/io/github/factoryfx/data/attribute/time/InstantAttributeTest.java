package io.github.factoryfx.data.attribute.time;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;

public class InstantAttributeTest {

    @Test
    public void test_json(){
        InstantAttribute attribute = new InstantAttribute();
        Instant value = Instant.now();
        attribute.set(value);
        InstantAttribute copy = ObjectMapperBuilder.build().copy(attribute);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
        Assertions.assertEquals(value,copy.get());
    }

    @Test
    public void parse_from_ts(){
        Instant parse = Instant.parse("2018-12-12T10:24:55.262Z");
        Assertions.assertEquals(262,parse.get(ChronoField.MILLI_OF_SECOND));
    }

}