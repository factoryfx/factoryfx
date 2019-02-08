package de.factoryfx.nanoservice;

import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.storageFactory.set(new SubscriptionStorageFactory());
        FactoryTreeBuilder<Void,Root,RootFactory,Void> builder =  new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON);
        builder.addFactory(SubscriptionStorageFactory.class, Scope.SINGLETON);

        Microservice<Void,Root,RootFactory,Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();
    }

}
