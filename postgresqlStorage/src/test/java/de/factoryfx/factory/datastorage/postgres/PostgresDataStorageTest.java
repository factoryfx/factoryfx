package de.factoryfx.factory.datastorage.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import javax.sql.DataSource;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.jdbc.AutoSave;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

public class PostgresDataStorageTest {

    static PostgresProcess postgresProcess;
    static DataSource postgresDatasource;
    @BeforeClass
    public static void setupPostgres() {
        try {
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
            PostgresDataStorageTest.postgresDatasource = Mockito.spy(postgresDatasource);
            Mockito.when(PostgresDataStorageTest.postgresDatasource.getConnection()).thenAnswer(new Answer<Connection>() {
                @Override
                public Connection answer(InvocationOnMock invocation) throws Throwable {
                    Connection connection = postgresDatasource.getConnection();
                    connection.setAutoCommit(false);
                    return connection;
                }
            });
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void stopPostgres() {
        postgresProcess.stop();
    }


    private MigrationManager<ExampleFactoryA,Void> createDataMigrationManager(){
        return new MigrationManager<>(ExampleFactoryA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager(), ObjectMapperBuilder.build());
    }

    @Test
    public void test_init_no_existing_factory() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        postgresFactoryStorage.getCurrentFactory();
        try (Connection con = postgresDatasource.getConnection()) {
            for (String sql : Arrays.asList("select * from currentconfiguration"
                    ,"select * from configurationmetadata"
                    ,"select * from configuration")) {
                PreparedStatement pstmt = con.prepareStatement(sql);
                try (ResultSet rs = pstmt.executeQuery()) {
                    Assert.assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    public void test_init_no_existing_factory_but_schema() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        try (Connection con = postgresDatasource.getConnection()) {
            postgresFactoryStorage.createTables(con);
            con.commit();
        }

        try (Connection con = postgresDatasource.getConnection()) {
            for (String sql : Arrays.asList("select * from currentconfiguration"
                    ,"select * from configurationmetadata"
                    ,"select * from configuration")) {
                PreparedStatement pstmt = con.prepareStatement(sql);
                try (ResultSet rs = pstmt.executeQuery()) {
                    Assert.assertFalse(sql,rs.next());
                }
            }
        }
    }

    private ExampleFactoryA createInitialExampleFactoryA() {
        ExampleFactoryA exampleDataA = new ExampleFactoryA();
        exampleDataA.internal().addBackReferences();
        return exampleDataA;
    }

    private DataUpdate<ExampleFactoryA> createUpdateExampleFactoryA() {
        ExampleFactoryA exampleDataA = new ExampleFactoryA();
        exampleDataA.internal().addBackReferences();
        return new DataUpdate<>(exampleDataA,"user","comment","13213");
    }

    @Test
    public void test_init_existing_factory() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        String id=postgresFactoryStorage.getCurrentFactory().id;

        PostgresDataStorage<ExampleFactoryA,Void> restored = new PostgresDataStorage<>(postgresDatasource, null,GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        Assert.assertEquals(id,restored.getCurrentFactory().id);
    }

    @Test
    public void test_update() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        String id=postgresFactoryStorage.getCurrentFactory().id;

        DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
        postgresFactoryStorage.updateCurrentFactory(update,null);
        Assert.assertNotEquals(id,postgresFactoryStorage.getCurrentFactory().id);
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());
        Assert.assertEquals(id,new ArrayList<>(postgresFactoryStorage.getHistoryFactoryList()).get(0).id);

    }

    @Before
    public void truncate() {
        try (Connection con = postgresDatasource.getConnection()) {
            for (String sql : Arrays.asList("drop table currentconfiguration"
                    ,"drop table configurationmetadata"
                    ,"drop table futureconfigurationmetadata"
                    ,"drop table futureconfiguration"
                    ,"drop table configuration")) {
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.execute();
            }
            con.commit();
        } catch (SQLException ignored) {//non-existent upon first call
        }
    }

    @Test
    public void test_initial_history() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());

        postgresFactoryStorage.getCurrentFactory();
        Assert.assertEquals(1,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        postgresFactoryStorage.getCurrentFactory();

        {
            DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
            postgresFactoryStorage.updateCurrentFactory(update,null);
        }

        {
            DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
            postgresFactoryStorage.updateCurrentFactory(update,null);
        }

        {
            DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
            postgresFactoryStorage.updateCurrentFactory(update,null);
        }

        Assert.assertEquals(4,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore(){
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        postgresFactoryStorage.getCurrentFactory();

        DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
        postgresFactoryStorage.updateCurrentFactory(update,null);
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());

        PostgresDataStorage<ExampleFactoryA,Void> restored = new PostgresDataStorage<>(postgresDatasource,createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());
        Assert.assertEquals(2,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_future() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());

        postgresFactoryStorage.getCurrentFactory();
        ScheduledUpdate<ExampleFactoryA> update = new ScheduledUpdate<>(
                new ExampleFactoryA(),
                "user",
                "comment",
                postgresFactoryStorage.getCurrentFactory().id,
                LocalDateTime.now()
        );
        update.root.internal().addBackReferences();

        postgresFactoryStorage.addFutureFactory(update);
        Collection<ScheduledUpdateMetadata> list = postgresFactoryStorage.getFutureFactoryList();
        Assert.assertEquals(1,list.size());
        String userFromStorage = list.iterator().next().user;
        Assert.assertEquals("user",userFromStorage);

        postgresFactoryStorage.getCurrentFactory();
        ScheduledUpdate<ExampleFactoryA> update2 = new ScheduledUpdate<>(
                new ExampleFactoryA(),
                "user",
                "comment",
                postgresFactoryStorage.getCurrentFactory().id,
                LocalDateTime.now()
        );

        postgresFactoryStorage.addFutureFactory(update2);
        list = postgresFactoryStorage.getFutureFactoryList();
        Assert.assertEquals(2,list.size());

        postgresFactoryStorage.deleteFutureFactory(list.iterator().next().id);
        list = postgresFactoryStorage.getFutureFactoryList();
        Assert.assertEquals(1,list.size());

        Assert.assertNotNull(postgresFactoryStorage.getFutureFactory(new ArrayList<>(list).get(0).id));
    }

    @Test
    public void test_history() {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager());

        DataUpdate<ExampleFactoryA> update = createUpdateExampleFactoryA();
        postgresFactoryStorage.updateCurrentFactory(update,null);
        Collection<StoredDataMetadata<Void>> list = postgresFactoryStorage.getHistoryFactoryList();
        Assert.assertEquals(1,list.size());
        String user = list.iterator().next().id;
        Assert.assertEquals(user,update.user);

        DataUpdate<ExampleFactoryA> update2 = createUpdateExampleFactoryA();
        postgresFactoryStorage.updateCurrentFactory(update2,null);
        list = postgresFactoryStorage.getHistoryFactoryList();
        Assert.assertEquals(2,list.size());

        Assert.assertNotNull(postgresFactoryStorage.getHistoryFactory(new ArrayList<>(list).get(0).id));

    }


}

