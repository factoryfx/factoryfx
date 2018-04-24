package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        root=root.utility().prepareUsableCopy();

        Microservice<Void,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(root));
        microservice.start();
        microservice.stop();

    }
}
