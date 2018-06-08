package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongAttributeTest {

    @Test
    public void test_json(){
        LongAttribute attribute = new LongAttribute();
        long value = 1;
        attribute.set(value);
        LongAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().longValue());
    }


}