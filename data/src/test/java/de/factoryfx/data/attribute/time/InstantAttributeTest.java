package de.factoryfx.data.attribute.time;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

public class InstantAttributeTest {

    @Test
    public void test_json(){
        InstantAttribute attribute = new InstantAttribute();
        Instant value = Instant.now();
        attribute.set(value);
        InstantAttribute copy = ObjectMapperBuilder.build().copy(attribute);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(attribute));
        Assert.assertEquals(value,copy.get());
    }

    @Test
    public void parse_from_ts(){
        Instant parse = Instant.parse("2018-12-12T10:24:55.262Z");
        Assert.assertEquals(262,parse.get(ChronoField.MILLI_OF_SECOND));
    }

}