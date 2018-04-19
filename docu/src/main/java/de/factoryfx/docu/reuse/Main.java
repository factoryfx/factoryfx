package de.factoryfx.docu.reuse;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();
        root.stringAttribute.set("1");
        root=root.utility().prepareUsableCopy();

        long start=System.currentTimeMillis();
        ApplicationServer<Void,RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<Void,RootFactory>(new RethrowingFactoryExceptionHandler<>()),new InMemoryDataStorage<>(root));
        applicationServer.start();

        //over 5000ms most time for the ExpensiveResource
        System.out.println(System.currentTimeMillis()-start);

        long updateStart=System.currentTimeMillis();
        DataAndNewMetadata<RootFactory> update = applicationServer.prepareNewFactory();
        update.root.stringAttribute.set("2");
        applicationServer.updateCurrentFactory(update, "", "", s -> true);

        //much less than the 5000ms => ExpensiveResource not recreated
        System.out.println(System.currentTimeMillis()-updateStart);

    }
}
