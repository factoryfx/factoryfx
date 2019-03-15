package de.factoryfx.docu.parametrized;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON,ctx->{
            RootFactory root = new RootFactory();
            //update to print system.out
            PrinterCreatorFactory printerCreatorFactory = new PrinterCreatorFactory();
            printerCreatorFactory.text.set("bla");
            root.printerCreator.set(printerCreatorFactory);
            return root;
        });

        Microservice<Root,RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

        //prints: 123::bla
        //"123" from the parameter and "bla" from the factory
    }
}
