package de.factoryfx.docu.persistentstorage;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.JacksonDeSerialisation;
import de.factoryfx.factory.datastorage.JacksonSerialisation;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.datastorage.postgres.DisableAutocommitDatasource;
import de.factoryfx.factory.datastorage.postgres.PostgresFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import org.postgresql.jdbc.AutoSave;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        //embedded postgrees db
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
        FactorySerialisationManager<RootFactory> serialisationManager = new FactorySerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(RootFactory.class,1),new ArrayList<>(),1);
        PostgresFactoryStorage<Void,Root, RootFactory> postgresFactoryStorage = new PostgresFactoryStorage<>(datasource, root, serialisationManager);


        ApplicationServer<Void,Root, RootFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),postgresFactoryStorage);
        applicationServer.start();
        //output is 1 from initial factory

        long updateStart=System.currentTimeMillis();
        FactoryAndNewMetadata<RootFactory> update = applicationServer.prepareNewFactory();
        update.root.stringAttribute.set("2");
        applicationServer.updateCurrentFactory(update, "", "", s -> true);
        //output is 2 from initial factory

        applicationServer.stop();
        ApplicationServer<Void,Root, RootFactory> newApplicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),postgresFactoryStorage);
        newApplicationServer.start();
        //output is 2 again from the saved update



        System.out.println("\n\n\n\n\n\n\n\n");

        postgresProcess.stop();


    }
}
