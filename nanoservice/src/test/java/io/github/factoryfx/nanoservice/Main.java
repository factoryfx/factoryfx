package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.storageFactory.set(new SubscriptionStorageFactory());
        FactoryTreeBuilder<Root,RootFactory,Void> builder =  new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON);
        builder.addFactory(SubscriptionStorageFactory.class, Scope.SINGLETON);

        Microservice<Root,RootFactory,Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();
    }

}
