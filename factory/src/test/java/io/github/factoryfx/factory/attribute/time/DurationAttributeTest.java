package io.github.factoryfx.factory.attribute.time;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;


public class DurationAttributeTest {

    private static class DurationAttributeFactory{
        public final DurationAttribute attribute = new DurationAttribute();
    }

    @Test
    public void test_Json(){
        DurationAttributeFactory durationAttributeFactory = new DurationAttributeFactory();
        durationAttributeFactory.attribute.set(Duration.ofDays(1));
        DurationAttributeFactory copy = ObjectMapperBuilder.build().copy(durationAttributeFactory);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(durationAttributeFactory));
        Assertions.assertEquals(copy.attribute.get(),durationAttributeFactory.attribute.get());
    }

}