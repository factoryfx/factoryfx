package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        root=root.utility().prepareUsableCopy();

        ApplicationServer<Void,RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<Void,RootFactory>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(root));
        applicationServer.start();
        applicationServer.stop();

    }
}
