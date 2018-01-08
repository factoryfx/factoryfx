package de.factoryfx.factory.datastorage.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import de.factoryfx.data.storage.*;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.postgresql.jdbc.AutoSave;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;
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
            Jdbc3SimpleDataSource _postgresDatasource = new Jdbc3SimpleDataSource();
            _postgresDatasource.setServerName(config.net().host());
            _postgresDatasource.setPortNumber(config.net().port());
            _postgresDatasource.setDatabaseName(config.storage().dbName());
            _postgresDatasource.setUser(config.credentials().username());
            _postgresDatasource.setPassword(config.credentials().password());
            _postgresDatasource.setAutosave(AutoSave.NEVER);
            postgresDatasource = Mockito.spy(_postgresDatasource);
            Mockito.when(postgresDatasource.getConnection()).thenAnswer(new Answer<Connection>() {
                @Override
                public Connection answer(InvocationOnMock invocation) throws Throwable {
                    Connection connection = _postgresDatasource.getConnection();
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


    private DataSerialisationManager<ExampleFactoryA,Void> createSerialisation(){
        int dataModelVersion = 1;
        return new DataSerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleFactoryA.class, dataModelVersion), Collections.emptyList(),1);
    }

    @Test
    public void test_init_no_existing_factory() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();
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
    public void test_init_no_existing_factory_but_schema() throws SQLException, IOException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, new ExampleFactoryA(),createSerialisation());
        try (Connection con = postgresDatasource.getConnection()) {
            postgresFactoryStorage.createTables(con);
            con.commit();
        }
        postgresFactoryStorage.loadInitialFactory();
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
    public void test_init_existing_factory() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();
        String id=postgresFactoryStorage.getCurrentFactory().metadata.id;

        PostgresDataStorage<ExampleFactoryA,Void> restored = new PostgresDataStorage<>(postgresDatasource, null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();
        String id=postgresFactoryStorage.getCurrentFactory().metadata.id;

        DataAndNewMetadata<ExampleFactoryA> update = new DataAndNewMetadata<>(new ExampleFactoryA(),new NewDataMetadata());
        postgresFactoryStorage.updateCurrentFactory(update,"","",null);
        Assert.assertNotEquals(id,postgresFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());
        Assert.assertEquals(id,new ArrayList<>(postgresFactoryStorage.getHistoryFactoryList()).get(0).id);

    }

    @Before
    public void truncate() throws SQLException {
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
    public void test_initial_history() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource,new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource,new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();

        {
            NewDataMetadata metadata = new NewDataMetadata();
            postgresFactoryStorage.updateCurrentFactory(new DataAndNewMetadata<>(new ExampleFactoryA(),metadata),"","",null);
        }

        {
            NewDataMetadata metadata = new NewDataMetadata();
            postgresFactoryStorage.updateCurrentFactory(new DataAndNewMetadata<>(new ExampleFactoryA(),metadata),"","",null);
        }

        {
            NewDataMetadata metadata = new NewDataMetadata();
            postgresFactoryStorage.updateCurrentFactory(new DataAndNewMetadata<>(new ExampleFactoryA(),metadata),"","",null);
        }

        Assert.assertEquals(4,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource,new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();

        NewDataMetadata metadata = new NewDataMetadata();
        postgresFactoryStorage.updateCurrentFactory(new DataAndNewMetadata<>(new ExampleFactoryA(),metadata),"","",null);
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());

        PostgresDataStorage<ExampleFactoryA,Void> restored = new PostgresDataStorage<>(postgresDatasource,new ExampleFactoryA(), createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(2,restored.getHistoryFactoryList().size());
    }

    @Test
    public void test_future() throws SQLException {
        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource,new ExampleFactoryA(),createSerialisation());
        postgresFactoryStorage.loadInitialFactory();

        {
            NewScheduledDataMetadata metadata = new NewScheduledDataMetadata(new NewDataMetadata(),LocalDateTime.now());
            ScheduledDataMetadata scheduledDataMetadata = postgresFactoryStorage.addFutureFactory(new ExampleFactoryA(), metadata, "", "",null);
            Collection<ScheduledDataMetadata<Void>> list = postgresFactoryStorage.getFutureFactoryList();
            Assert.assertEquals(1,list.size());
            String id = list.iterator().next().id;
            Assert.assertEquals(id,scheduledDataMetadata.id);
            metadata = new NewScheduledDataMetadata(new NewDataMetadata(),LocalDateTime.now());
            postgresFactoryStorage.addFutureFactory(new ExampleFactoryA(),metadata,"","",null);
            list = postgresFactoryStorage.getFutureFactoryList();
            Assert.assertEquals(2,list.size());
            postgresFactoryStorage.deleteFutureFactory(list.iterator().next().id);
            list = postgresFactoryStorage.getFutureFactoryList();
            Assert.assertEquals(1,list.size());

        }


    }


}

