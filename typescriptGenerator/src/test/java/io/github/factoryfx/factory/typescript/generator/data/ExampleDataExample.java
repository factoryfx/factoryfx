package io.github.factoryfx.factory.typescript.generator.data;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

public class ExampleDataExample {
    public static void main(String[] args) {
        ExampleData data = new ExampleData();
        data.attribute.set("123");
        ExampleData2 exampleData2 = new ExampleData2();
        data.ref.set(exampleData2);
        data.refList.add(new ExampleData2());
        data.refList.add(exampleData2);


        System.out.println(ObjectMapperBuilder.build().writeValueAsString(data));
    }
}
