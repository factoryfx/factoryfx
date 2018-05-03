package de.factoryfx.nanoservice;

import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.storageFactory.set(new SubscriptionStorageFactory());
        Microservice<Void,RootFactory,Void> microService = new Microservice<Void,RootFactory,Void>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(rootFactory.utility().prepareUsableCopy()));
        microService.start();
    }

}
