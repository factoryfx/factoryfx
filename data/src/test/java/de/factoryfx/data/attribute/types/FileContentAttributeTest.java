package de.factoryfx.data.attribute.types;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class FileContentAttributeTest {

    @Test
    public void test_json(){
        FileContentAttribute attribute= new FileContentAttribute();
        attribute.set(new byte[1]);
        FileContentAttribute copy= ObjectMapperBuilder.build().copy(attribute);
        Assert.assertArrayEquals(new byte[1],copy.get());
    }

}