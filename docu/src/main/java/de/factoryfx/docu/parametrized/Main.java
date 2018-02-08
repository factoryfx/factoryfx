package de.factoryfx.docu.parametrized;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        //update to print system.out
        PrinterCreatorFactory printerCreatorFactory = new PrinterCreatorFactory();
        printerCreatorFactory.text.set("bla");
        root.printerCreator.set(printerCreatorFactory);
        root=root.utility().prepareUsableCopy();

        ApplicationServer<Void,Root,RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(root));
        applicationServer.start();

        //prints: 123::bla
        //"123" from the parameter and "bla" from the factory
    }
}
