package de.factoryfx.docu.persistentstorage;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.data.storage.JacksonSerialisation;
import de.factoryfx.factory.datastorage.postgres.DisableAutocommitDatasource;
import de.factoryfx.factory.datastorage.postgres.PostgresDataStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import org.postgresql.jdbc.AutoSave;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        //embedded postgres db
        PostgresProcess postgresProcess;
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        final PostgresConfig config = PostgresConfig.defaultWithDbName("test","testuser","testpw");
        PostgresExecutable exec = runtime.prepare(config);
        postgresProcess = exec.start();
        Jdbc3SimpleDataSource postgresDatasource = new Jdbc3SimpleDataSource();
        postgresDatasource.setServerName(config.net().host());
        postgresDatasource.setPortNumber(config.net().port());
        postgresDatasource.setDatabaseName(config.storage().dbName());
        postgresDatasource.setUser(config.credentials().username());
        postgresDatasource.setPassword(config.credentials().password());
        postgresDatasource.setAutosave(AutoSave.NEVER);
        DisableAutocommitDatasource datasource = new DisableAutocommitDatasource(postgresDatasource);

        System.out.println("\n\n\n\n\n\n\n\n");

        RootFactory root = new RootFactory();
        root.stringAttribute.set("1");
        DataSerialisationManager<RootFactory,Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(RootFactory.class,1),new ArrayList<>(),1);
        PostgresDataStorage<RootFactory,Void> postgresFactoryStorage = new PostgresDataStorage<>(datasource, root, serialisationManager);


        ApplicationServer<Void, RootFactory,Void> applicationServer = new ApplicationServer<>(new FactoryManager<Void, RootFactory>(new RethrowingFactoryExceptionHandler<>()),postgresFactoryStorage);
        applicationServer.start();
        //output is 1 from initial factory

        DataAndNewMetadata<RootFactory> update = applicationServer.prepareNewFactory();
        update.root.stringAttribute.set("2");
        applicationServer.updateCurrentFactory(update, "", "", s -> true);
        //output is 2 from initial factory

        applicationServer.stop();
        ApplicationServer<Void, RootFactory,Void> newApplicationServer = new ApplicationServer<>(new FactoryManager<Void, RootFactory>(new RethrowingFactoryExceptionHandler<>()),postgresFactoryStorage);
        newApplicationServer.start();
        //output is 2 again from the saved update



        System.out.println("\n\n\n\n\n\n\n\n");
        postgresProcess.stop();


    }
}
