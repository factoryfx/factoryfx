package de.factoryfx.docu.dependencyinjection;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.dependency.set(new DependencyFactory());

        Microservice<Void,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(rootFactory));
        microservice.start();

    }
}
