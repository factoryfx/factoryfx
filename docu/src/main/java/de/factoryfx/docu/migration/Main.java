package de.factoryfx.docu.migration;

import de.factoryfx.data.storage.*;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.filesystem.FileSystemDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        RootFactory rootFactory = new RootFactory();
        rootFactory.text.set("HelloWorld");

        //for every incompatible change the dataModelVersion must be adjusted and the migration added to the list
        int dataModelVersion = 2;
        List<DataMigration> dataMigrations = new ArrayList<>();
        dataMigrations.add(new SimpleDataMigration(1, 2, old -> {
            return old;//do nothing it's just simple example
        }));

        DataSerialisationManager<RootFactory,Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(RootFactory.class, dataModelVersion), dataMigrations,dataModelVersion);
        FileSystemDataStorage<RootFactory,Void> fileSystemFactoryStorage = new FileSystemDataStorage<>(Files.createTempDirectory("tempfiles"), rootFactory, serialisationManager);

        Microservice<Void,RootFactory,Void> microservice = new Microservice<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler()), fileSystemFactoryStorage);
        microservice.start();

    }
}
