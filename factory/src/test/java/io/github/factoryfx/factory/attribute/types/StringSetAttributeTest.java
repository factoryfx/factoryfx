package io.github.factoryfx.factory.attribute.types;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringSetAttributeTest {
    public static class ExampleSetFactory extends FactoryBase<Void, ExampleSetFactory> {
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
        Assertions.assertEquals("123",copy.get().iterator().next());
    }

}