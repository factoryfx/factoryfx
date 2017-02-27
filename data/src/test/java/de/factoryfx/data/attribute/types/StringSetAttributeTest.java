package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Test;

public class StringSetAttributeTest {
    public static class ExampleSetFactory extends IdData {
        public StringSetAttribute setAttribute =new StringSetAttribute(new AttributeMetadata());
    }

    @Test
    public void test_json(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        exampleSetFactory.setAttribute.get().add("7787");
        ObjectMapperBuilder.build().copy(exampleSetFactory);
    }
}