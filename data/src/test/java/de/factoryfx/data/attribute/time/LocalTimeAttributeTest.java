package de.factoryfx.data.attribute.time;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

public class LocalTimeAttributeTest {

    @Test
    public void test_json(){
        LocalTimeAttribute attribute = new LocalTimeAttribute();
        LocalTime value = LocalTime.now();
        attribute.set(value);
        LocalTimeAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get());
    }


}