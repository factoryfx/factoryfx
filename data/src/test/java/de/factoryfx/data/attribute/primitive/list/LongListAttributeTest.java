package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongListAttributeTest {

    @Test
    public void test_json(){
        LongListAttribute attribute = new LongListAttribute();
        long value = 1;
        attribute.add(value);
        LongListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(1,copy.get(0).longValue());
    }

}