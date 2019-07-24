package io.github.factoryfx.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

class ParameterlessFactoryTest {
    public static class Test123 {

    }

    public static class ExampleParameterlessFactory extends ParameterlessFactory<Test123, ExampleParameterlessFactory> {

        @JsonCreator
        public ExampleParameterlessFactory(@JsonProperty("clazz")Class<? extends Test123> clazz) {
            this.clazz=clazz;
        }
    }

    @Test
    public void test_json(){
        ExampleParameterlessFactory exampleParamterlessFactory =new ExampleParameterlessFactory(Test123.class);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleParamterlessFactory
        ));
        ObjectMapperBuilder.build().copy(exampleParamterlessFactory);
    }
}