package de.factoryfx.docu.helloworld;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        helloWorldFactory.text.set("HelloWorld");

        ApplicationServer<Void,HelloWorld,HelloWorldFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryFactoryStorage<>(helloWorldFactory));
        applicationServer.start();

    }
}
