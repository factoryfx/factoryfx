package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;

public class ExampleFactoryExample {
    public static void main(String[] args) {
        ExampleFactory data = new ExampleFactory();
        data.attribute.set("123");
        data.ref.set(new ExampleFactory());
        data.refList.add(new ExampleFactory());

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(data));
    }
}
