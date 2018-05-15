package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        //update to print system.out
        root.printer.set(new DefaultPrinterFactory());
        root=root.utility().prepareUsableCopy();

        Microservice<Void,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(root));
        microservice.start();

        DataAndNewMetadata<RootFactory> update = microservice.prepareNewFactory();
        //update to print on error out
        update.root.printer.set(new ErrorPrinterFactory());
        microservice.updateCurrentFactory(update, "", "", s -> true);

    }
}
