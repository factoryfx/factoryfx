package de.factoryfx.data.attribute.types;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.time.LocalTimeAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

public class StringMapAttributeTest {
    public static class ExampleMapFactory extends Data {
        public StringMapAttribute mapAttribute =new StringMapAttribute();
    }

    @Test
    public void test_json_inside_data(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        exampleMapFactory.mapAttribute.put("7787","dgdgf");
        ObjectMapperBuilder.build().copy(exampleMapFactory);
    }

    @Test
    public void test_json(){
        StringMapAttribute attribute = new StringMapAttribute();
        attribute.put("7787","dgdgf");
        StringMapAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals("dgdgf",copy.get("7787"));
    }
}