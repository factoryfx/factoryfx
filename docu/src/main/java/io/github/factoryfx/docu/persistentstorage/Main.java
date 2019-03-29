package io.github.factoryfx.docu.persistentstorage;

import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.datastorage.postgres.DisableAutocommitDatasource;
import io.github.factoryfx.factory.datastorage.postgres.PostgresDataStorage;
import io.github.factoryfx.server.Microservice;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.jdbc.AutoSave;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        //embedded postgres db
        PostgresProcess postgresProcess;
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        final PostgresConfig config = PostgresConfig.defaultWithDbName("test","testuser","testpw");
        PostgresExecutable exec = runtime.prepare(config);
        postgresProcess = exec.start();
        PGSimpleDataSource postgresDatasource = new PGSimpleDataSource();
        postgresDatasource.setServerName(config.net().host());
        postgresDatasource.setPortNumber(config.net().port());
        postgresDatasource.setDatabaseName(config.storage().dbName());
        postgresDatasource.setUser(config.credentials().username());
        postgresDatasource.setPassword(config.credentials().password());
        postgresDatasource.setAutosave(AutoSave.NEVER);
        DisableAutocommitDatasource datasource = new DisableAutocommitDatasource(postgresDatasource);

        System.out.println("\n\n\n\n\n\n\n\n");


        FactoryTreeBuilder< Root, RootFactory,Void> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, xtc -> {
            RootFactory root = new RootFactory();
            root.stringAttribute.set("1");
            return root;
        });
        Microservice<Root, RootFactory, Void> microservice = builder.microservice().withStorage((initialFactory, migrationManager, objectMapper) -> {
            return new PostgresDataStorage<>(datasource, initialFactory, migrationManager, objectMapper);
        }).build();
        microservice.start();

        //output is 1 from initial factory

        DataUpdate<RootFactory> update = microservice.prepareNewFactory();
        update.root.stringAttribute.set("2");
        microservice.updateCurrentFactory(update);
        //output is 2 from initial factory

        microservice.stop();

        Microservice<Root, RootFactory,Void> newMicroservice = builder.microservice().withStorage((initialFactory, migrationManager, objectMapper) -> {
            return new PostgresDataStorage<>(datasource, initialFactory, migrationManager, objectMapper);
        }).build();
        newMicroservice.start();
        //output is 2 again from the saved update



        System.out.println("\n\n\n\n\n\n\n\n");
        postgresProcess.stop();


    }
}
