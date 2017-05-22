package de.factoryfx.docu.migration;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.*;
import de.factoryfx.factory.datastorage.filesystem.FileSystemFactoryStorage;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws IOException {
        RootFactory rootFactory = new RootFactory();
        rootFactory.text.set("HelloWorld");

        //for every incompatible change the dataModelVersion must be adjusted and the migration added to the list
        int dataModelVersion = 2;
        List<FactoryMigration> factoryMigrations = new ArrayList<>();
        factoryMigrations.add(new SimpleFactoryMigration(1, 2, old -> {
            return old;//do nothing it's just simple example
        }));

        FactorySerialisationManager<RootFactory> serialisationManager = new FactorySerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(RootFactory.class, dataModelVersion), factoryMigrations,dataModelVersion);
        FileSystemFactoryStorage<Void, Root, RootFactory> fileSystemFactoryStorage = new FileSystemFactoryStorage<>(Files.createTempDirectory("tempfiles"), rootFactory, serialisationManager);

        ApplicationServer<Void,Root,RootFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), fileSystemFactoryStorage);
        applicationServer.start();

    }
}
