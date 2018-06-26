package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ShortListAttributeTest {

    @Test
    public void test_json(){
        ShortListAttribute attribute = new ShortListAttribute();
        short value = 1;
        attribute.add(value);
        ShortListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(1,copy.get(0).shortValue());
    }

}