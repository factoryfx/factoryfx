package io.github.factoryfx.docu.encryptedattributes;

import io.github.factoryfx.factory.attribute.types.EncryptedStringAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static final String KEY = "YONWc4GmsXIRPWaMb54QPA=="; //must not be part of a factory

    public static void main(String[] args) {
        //example how to generate a key
        System.out.println(":ExampleKey: " + EncryptedStringAttribute.createKey());

        FactoryTreeBuilder<Printer, PrinterFactory> builder = new FactoryTreeBuilder<>(PrinterFactory.class, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.password.set("Hello World", KEY);
            return factory;
        });

        Microservice<Printer, PrinterFactory> microservice = builder.microservice().build();
        microservice.start().print();

        PrinterFactory update = microservice.prepareNewFactory().root;
        String updateString = ObjectMapperBuilder.build().writeValueAsString(update);
        System.out.println("update does not contain the secret text: "+!updateString.contains("Hello World"));
    }
}
