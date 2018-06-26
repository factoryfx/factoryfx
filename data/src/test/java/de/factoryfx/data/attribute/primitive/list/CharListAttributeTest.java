package de.factoryfx.data.attribute.primitive.list;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class CharListAttributeTest {
    @Test
    public void test_json(){
        CharListAttribute attribute = new CharListAttribute();
        char value = '1';
        attribute.add(value);
        CharListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get(0).charValue());
    }
}