package de.factoryfx.factory.attribute.util;

import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayAttributeTest {

    @Test
    public void test_json(){
        ByteArrayAttribute attribute= new ByteArrayAttribute(new AttributeMetadata()).defaultValue(new Byte[]{1,2});
        ByteArrayAttribute copy= ObjectMapperBuilder.build().copy(attribute);

        Assert.assertArrayEquals(new Byte[]{1,2},copy.get());
    }


}