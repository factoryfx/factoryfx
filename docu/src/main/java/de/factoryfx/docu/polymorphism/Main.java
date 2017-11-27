package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        //update to print system.out
        root.printer.set(new DefaultPrinterFactory());

        ApplicationServer<Void,Root,RootFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(root));
        applicationServer.start();

        DataAndNewMetadata<RootFactory> update = applicationServer.prepareNewFactory();
        //update to print on error out
        update.root.printer.set(new ErrorPrinterFactory());
        applicationServer.updateCurrentFactory(update, "", "", s -> true);

    }
}
