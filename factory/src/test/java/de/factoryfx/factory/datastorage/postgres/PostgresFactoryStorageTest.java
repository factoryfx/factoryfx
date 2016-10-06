package de.factoryfx.factory.datastorage.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.sql.DataSource;

import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
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

public class PostgresFactoryStorageTest {

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

    @Test
    public void test_init_no_existing_factory() throws SQLException {
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource, new ExampleFactoryA(),ExampleFactoryA.class);
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
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource, new ExampleFactoryA(),ExampleFactoryA.class);
        postgresFactoryStorage.loadInitialFactory();
        String id=postgresFactoryStorage.getCurrentFactory().metadata.id;

        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> restored = new PostgresFactoryStorage<>(postgresDatasource, null,ExampleFactoryA.class);
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws SQLException {
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource, new ExampleFactoryA(),ExampleFactoryA.class);
        postgresFactoryStorage.loadInitialFactory();
        String id=postgresFactoryStorage.getCurrentFactory().metadata.id;

        FactoryAndStorageMetadata<ExampleFactoryA> update = new FactoryAndStorageMetadata<>(new ExampleFactoryA(),new StoredFactoryMetadata());
        update.metadata.id = UUID.randomUUID().toString();
        postgresFactoryStorage.updateCurrentFactory(update);
        Assert.assertNotEquals(id,postgresFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());
        Assert.assertEquals(id,new ArrayList<>(postgresFactoryStorage.getHistoryFactoryList()).get(0).id);

    }

    @Before
    public void truncate() throws SQLException {
        try (Connection con = postgresDatasource.getConnection()) {
            for (String sql : Arrays.asList("drop table currentconfiguration"
                    ,"drop table configurationmetadata"
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
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource,new ExampleFactoryA(),ExampleFactoryA.class);
        postgresFactoryStorage.loadInitialFactory();

        Assert.assertEquals(1,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws SQLException {
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource,new ExampleFactoryA(),ExampleFactoryA.class);
        postgresFactoryStorage.loadInitialFactory();

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            postgresFactoryStorage.updateCurrentFactory(new FactoryAndStorageMetadata<>(new ExampleFactoryA(),metadata));
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            postgresFactoryStorage.updateCurrentFactory(new FactoryAndStorageMetadata<>(new ExampleFactoryA(),metadata));
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            postgresFactoryStorage.updateCurrentFactory(new FactoryAndStorageMetadata<>(new ExampleFactoryA(),metadata));
        }

        Assert.assertEquals(4,postgresFactoryStorage.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws SQLException {
        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> postgresFactoryStorage = new PostgresFactoryStorage<>(postgresDatasource,new ExampleFactoryA(),ExampleFactoryA.class);
        postgresFactoryStorage.loadInitialFactory();

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        postgresFactoryStorage.updateCurrentFactory(new FactoryAndStorageMetadata<>(new ExampleFactoryA(),metadata));
        Assert.assertEquals(2,postgresFactoryStorage.getHistoryFactoryList().size());

        PostgresFactoryStorage<ExampleLiveObjectA,Void, ExampleFactoryA> restored = new PostgresFactoryStorage<>(postgresDatasource,new ExampleFactoryA(), ExampleFactoryA.class);
        restored.loadInitialFactory();
        Assert.assertEquals(2,restored.getHistoryFactoryList().size());
    }

}

