package de.factoryfx.docu.dependencyinjection;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        root.dependency.set(new DependencyFactory());

        ApplicationServer<Void,Root,RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(root));
        applicationServer.start();

    }
}
