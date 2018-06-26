package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ShortAttributeTest {


    @Test
    public void test_json(){
        ShortAttribute attribute = new ShortAttribute();
        short value = 1;
        attribute.set(value);
        ShortAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().shortValue());
    }


}