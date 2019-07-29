package io.github.factoryfx.docu.parametrized;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            RootFactory root = new RootFactory();
            //update to print system.out
            PrinterCreatorFactory printerCreatorFactory = new PrinterCreatorFactory();
            printerCreatorFactory.text.set("bla");
            root.printerCreator.set(printerCreatorFactory);
            return root;
        });

        Microservice<Root,RootFactory> microservice = builder.microservice().build();
        microservice.start();

        //prints: 123::bla
        //"123" from the parameter and "bla" from the factory
    }
}
