package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.storageFactory.set(new SubscriptionStorageFactory());
        FactoryTreeBuilder<Root,RootFactory> builder =  new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(SubscriptionStorageFactory.class, Scope.SINGLETON);

        Microservice<Root,RootFactory> microService = builder.microservice().build();
        microService.start();
    }

}
