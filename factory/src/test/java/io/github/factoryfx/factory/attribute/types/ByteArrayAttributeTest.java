package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteArrayAttributeTest {

    @Test
    public void test_json(){
        ByteArrayAttribute attribute= new ByteArrayAttribute().defaultValue(new byte[]{1,2});
        ByteArrayAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(copy));


        Assertions.assertArrayEquals(new byte[]{1,2},copy.get());
    }


}