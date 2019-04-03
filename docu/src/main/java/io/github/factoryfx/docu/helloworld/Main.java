package io.github.factoryfx.docu.helloworld;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Printer, PrinterFactory,Void> builder = new FactoryTreeBuilder<>(PrinterFactory.class, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            return factory;
        });

        builder.microservice().withInMemoryStorage().build().start();
    }
}
