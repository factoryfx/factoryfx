package de.factoryfx.docu.parametrized;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        //update to print system.out
        PrinterCreatorFactory printerCreatorFactory = new PrinterCreatorFactory();
        printerCreatorFactory.text.set("bla");
        root.printerCreator.set(printerCreatorFactory);

        Microservice<Void,Root,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(root));
        microservice.start();

        //prints: 123::bla
        //"123" from the parameter and "bla" from the factory
    }
}
