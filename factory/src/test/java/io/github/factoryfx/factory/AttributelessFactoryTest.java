package io.github.factoryfx.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

class AttributelessFactoryTest {
    public static class Test123 {

    }

    public static class ExampleAttributelessFactory extends AttributelessFactory<Test123, ExampleAttributelessFactory> {

        @JsonCreator
        public ExampleAttributelessFactory(@JsonProperty("clazz")Class<? extends Test123> clazz) {
            this.clazz=clazz;
        }
    }

    @Test
    public void test_json(){
        ExampleAttributelessFactory exampleParamterlessFactory =new ExampleAttributelessFactory(Test123.class);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleParamterlessFactory
        ));
        ObjectMapperBuilder.build().copy(exampleParamterlessFactory);
    }
}