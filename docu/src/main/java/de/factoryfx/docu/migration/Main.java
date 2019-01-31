package de.factoryfx.docu.migration;

import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.DataMigration;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.migration.DataMigrationApi;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws IOException {
        RootFactory rootFactory = new RootFactory();
        rootFactory.text.set("HelloWorld");

        List<DataMigration> dataMigrations = new ArrayList<>();
        dataMigrations.add(new DataMigration(new Consumer<DataMigrationApi>() {
            @Override
            public void accept(DataMigrationApi dataMigrationApi) {
                //do nothing it's just simple example
            }
        }));

        MigrationManager<RootFactory,Void> serialisationManager = new MigrationManager<RootFactory,Void>(RootFactory.class, List.of(), GeneralStorageMetadataBuilder.build(), dataMigrations, new DataStorageMetadataDictionary(RootFactory.class));

        FileSystemDataStorage<RootFactory,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Files.createTempDirectory("tempfiles"), rootFactory, serialisationManager);

        Microservice<Void,Root,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), fileSystemFactoryStorage);
        microservice.start();

    }
}
