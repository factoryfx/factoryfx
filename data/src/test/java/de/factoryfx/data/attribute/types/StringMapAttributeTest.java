package de.factoryfx.data.attribute.types;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class StringMapAttributeTest {
    public static class ExampleMapFactory extends Data {
        public StringMapAttribute mapAttribute =new StringMapAttribute(new AttributeMetadata());
    }

    @Test
    public void test_json(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        exampleMapFactory.mapAttribute.get().put("7787","dgdgf");
        ObjectMapperBuilder.build().copy(exampleMapFactory);
    }
}