package de.factoryfx.data.attribute.time;

import java.time.Duration;

import org.junit.Assert;
import org.junit.Test;

import de.factoryfx.data.jackson.ObjectMapperBuilder;


public class DurationAttributeTest {

    @Test
    public void test_Json(){
        DurationAttribute attribute = new DurationAttribute();
        attribute.set(Duration.ofDays(1));
        DurationAttribute r = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(r.get(),attribute.get());
    }

}