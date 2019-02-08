package de.factoryfx.docu.migration;

import de.factoryfx.data.storage.migration.DataMigration;
import de.factoryfx.data.storage.migration.DataMigrationApi;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws IOException {

        FactoryTreeBuilder<Void,Root,RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx->{
            RootFactory rootFactory = new RootFactory();
            rootFactory.text.set("HelloWorld");
            return rootFactory;
        });
        Microservice<Void,Root,RootFactory,Void> microservice = builder.microservice().
                withDataMigration(new DataMigration(new Consumer<DataMigrationApi>() {
                    @Override
                    public void accept(DataMigrationApi dataMigrationApi) {
                        //do nothing it's just simple example
                    }
                })).
                withFilesystemStorage(Files.createTempDirectory("tempfiles")).build();

        microservice.start();

    }
}
