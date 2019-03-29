package io.github.factoryfx.factory.attribute.time;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;


public class DurationAttributeTest {

    @Test
    public void test_Json(){
        DurationAttribute attribute = new DurationAttribute();
        attribute.set(Duration.ofDays(1));
        DurationAttribute r = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(r.get(),attribute.get());
    }

}