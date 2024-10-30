package io.github.factoryfx.docu.persistentstorage;

import java.io.IOException;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.datastorage.postgres.DisableAutocommitDatasource;
import io.github.factoryfx.factory.datastorage.postgres.PostgresDataStorage;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

public class Main {

    public static void main(String[] args) throws IOException {
        //embedded postgres db
        try (var db = EmbeddedPostgres.start()) {

            DisableAutocommitDatasource datasource = new DisableAutocommitDatasource(db.getPostgresDatabase());

            System.out.println("\n\n\n\n\n\n\n\n");

            FactoryTreeBuilder<Root, RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class, xtc -> {
                RootFactory root = new RootFactory();
                root.stringAttribute.set("1");
                return root;
            });
            Microservice<Root, RootFactory> microservice = builder.microservice().withStorage((initialFactory, migrationManager, objectMapper) -> {
                return new PostgresDataStorage<>(datasource, initialFactory, migrationManager, objectMapper);
            }).build();
            microservice.start();

            //output is 1 from initial factory

            DataUpdate<RootFactory> update = microservice.prepareNewFactory();
            update.root.stringAttribute.set("2");
            microservice.updateCurrentFactory(update);
            //output is 2 from initial factory

            microservice.stop();

            Microservice<Root, RootFactory> newMicroservice = builder.microservice().withStorage((initialFactory, migrationManager, objectMapper) -> {
                return new PostgresDataStorage<>(datasource, initialFactory, migrationManager, objectMapper);
            }).build();
            newMicroservice.start();
            //output is 2 again from the saved update

            System.out.println("\n\n\n\n\n\n\n\n");

        }
    }
}
