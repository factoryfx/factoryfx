package de.factoryfx.docu.datainjection;


import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        FactoryTreeBuilder<Void,Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx-> {
            RootFactory rootFactory = new RootFactory();
            rootFactory.text.set("HelloWorld");
            return rootFactory;
        });

        Microservice<Void,Root,RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();

    }
}
