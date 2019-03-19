package io.github.factoryfx.data.attribute.types;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals("dgdgf",copy.get("7787"));
    }
}