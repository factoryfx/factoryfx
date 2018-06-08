package de.factoryfx.docu.reuse;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        root.stringAttribute.set("1");

        long start=System.currentTimeMillis();
        Microservice<Void,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),new InMemoryDataStorage<>(root));
        microservice.start();

        //over 5000ms most time for the ExpensiveResource
        System.out.println(System.currentTimeMillis()-start);

        long updateStart=System.currentTimeMillis();
        DataAndNewMetadata<RootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("2");
        microservice.updateCurrentFactory(update, "", "", s -> true);

        //much less than the 5000ms => ExpensiveResource not recreated
        System.out.println(System.currentTimeMillis()-updateStart);

    }
}
