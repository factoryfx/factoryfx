package de.factoryfx.data.attribute.types;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class StringSetAttributeTest {
    public static class ExampleSetFactory extends Data {
        public StringSetAttribute setAttribute =new StringSetAttribute();
    }

    @Test
    public void test_json_inside_data(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        exampleSetFactory.setAttribute.add("7787");
        ObjectMapperBuilder.build().copy(exampleSetFactory);
    }

    @Test
    public void test_json(){
        StringSetAttribute attribute = new StringSetAttribute();
        attribute.add("123");
        StringSetAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("123",copy.get().iterator().next());
    }

}