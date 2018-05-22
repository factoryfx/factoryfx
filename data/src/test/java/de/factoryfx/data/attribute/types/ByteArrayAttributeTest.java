package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ByteArrayAttributeTest {

    @Test
    public void test_json(){
        ByteArrayAttribute attribute= new ByteArrayAttribute().defaultValue(new byte[]{1,2});
        ByteArrayAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(copy));


        Assert.assertArrayEquals(new byte[]{1,2},copy.get());
    }


}