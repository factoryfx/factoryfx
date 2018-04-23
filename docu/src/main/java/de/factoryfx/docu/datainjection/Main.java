package de.factoryfx.docu.datainjection;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory rootFactory = new RootFactory();
        rootFactory.text.set("HelloWorld");
        rootFactory=rootFactory.utility().prepareUsableCopy();

        ApplicationServer<Void,RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(rootFactory));
        applicationServer.start();

    }
}
