package de.factoryfx.factory.attribute.util;

import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayAttributeTest {

    @Test
    public void test_json(){
        ByteArrayAttribute attribute= new ByteArrayAttribute(new AttributeMetadata()).defaultValue(new byte[]{1,2});
        ByteArrayAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(copy));


        Assert.assertArrayEquals(new byte[]{1,2},copy.get());
    }


}