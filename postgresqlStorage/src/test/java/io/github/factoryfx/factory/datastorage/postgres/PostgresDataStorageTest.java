package io.github.factoryfx.factory.datastorage.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.ScheduledUpdate;
import io.github.factoryfx.factory.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectB;
import org.junit.jupiter.api.*;
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
    @BeforeAll
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

    @AfterAll
    public static void stopPostgres() {
        postgresProcess.stop();
    }


    private MigrationManager<ExampleFactoryA> createDataMigrationManager(){
        return new MigrationManager<>(ExampleFactoryA.class, ObjectMapperBuilder.build(), (root, d) -> { });
    }

    @Test
    public void test_init_no_existing_factory() throws SQLException {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        postgresFactoryStorage.getCurrentData();
        try (Connection con = postgresDatasource.getConnection()) {
            for (String sql : Arrays.asList("select * from currentconfiguration"
                    ,"select * from configurationmetadata"
                    ,"select * from configuration")) {
                PreparedStatement pstmt = con.prepareStatement(sql);
                try (ResultSet rs = pstmt.executeQuery()) {
                    Assertions.assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    public void test_init_no_existing_factory_but_schema() throws SQLException {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
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
                    Assertions.assertFalse(rs.next(),sql);
                }
            }
        }
    }

    private ExampleFactoryA createInitialExampleFactoryA() {
        ExampleFactoryA exampleDataA = new ExampleFactoryA();
        exampleDataA.internal().finalise();
        return exampleDataA;
    }

    private DataUpdate<ExampleFactoryA> createUpdate() {
        ExampleFactoryA exampleDataA = new ExampleFactoryA();
        exampleDataA.internal().finalise();
        return new DataUpdate<>(exampleDataA,"user","comment","13213");
    }

    @Test
    public void test_init_existing_factory() {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=postgresFactoryStorage.getCurrentData().id;

        PostgresDataStorage<ExampleFactoryA> restored = new PostgresDataStorage<>(postgresDatasource, null, createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertEquals(id,restored.getCurrentData().id);
    }

    @Test
    public void test_update() {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=postgresFactoryStorage.getCurrentData().id;

        DataUpdate<ExampleFactoryA> update = createUpdate();
        postgresFactoryStorage.updateCurrentData(update,null);
        Assertions.assertNotEquals(id,postgresFactoryStorage.getCurrentData().id);
        Assertions.assertEquals(2,postgresFactoryStorage.getHistoryDataList().size());
        Assertions.assertEquals(id,new ArrayList<>(postgresFactoryStorage.getHistoryDataList()).get(0).id);

    }

    @BeforeEach
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
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());

        postgresFactoryStorage.getCurrentData();
        Assertions.assertEquals(1,postgresFactoryStorage.getHistoryDataList().size());
    }

    @Test
    public void test_multi_add() {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        postgresFactoryStorage.getCurrentData();

        {
            DataUpdate<ExampleFactoryA> update = createUpdate();
            postgresFactoryStorage.updateCurrentData(update,null);
        }

        {
            DataUpdate<ExampleFactoryA> update = createUpdate();
            postgresFactoryStorage.updateCurrentData(update,null);
        }

        {
            DataUpdate<ExampleFactoryA> update = createUpdate();
            postgresFactoryStorage.updateCurrentData(update,null);
        }

        Assertions.assertEquals(4,postgresFactoryStorage.getHistoryDataList().size());
    }

    @Test
    public void test_restore(){
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        postgresFactoryStorage.getCurrentData();

        DataUpdate<ExampleFactoryA> update = createUpdate();
        postgresFactoryStorage.updateCurrentData(update,null);
        Assertions.assertEquals(2,postgresFactoryStorage.getHistoryDataList().size());

        PostgresDataStorage<ExampleFactoryA> restored = new PostgresDataStorage<>(postgresDatasource,createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertEquals(2,restored.getHistoryDataList().size());
    }

    @Test
    public void test_future() {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());

        postgresFactoryStorage.getCurrentData();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.internal().finalise();
        ScheduledUpdate<ExampleFactoryA> update = new ScheduledUpdate<>(
                exampleFactoryA,
                "user",
                "comment",
                postgresFactoryStorage.getCurrentData().id,
                LocalDateTime.now()
        );
        update.root.internal().finalise();

        postgresFactoryStorage.addFutureData(update);
        Collection<ScheduledUpdateMetadata> list = postgresFactoryStorage.getFutureDataList();
        Assertions.assertEquals(1,list.size());
        String userFromStorage = list.iterator().next().user;
        Assertions.assertEquals("user",userFromStorage);

        postgresFactoryStorage.getCurrentData();
        ExampleFactoryA exampleFactoryA2 = new ExampleFactoryA();
        exampleFactoryA2.internal().finalise();
        ScheduledUpdate<ExampleFactoryA> update2 = new ScheduledUpdate<>(exampleFactoryA2,
                "user",
                "comment",
                postgresFactoryStorage.getCurrentData().id,
                LocalDateTime.now()
        );

        postgresFactoryStorage.addFutureData(update2);
        list = postgresFactoryStorage.getFutureDataList();
        Assertions.assertEquals(2,list.size());

        postgresFactoryStorage.deleteFutureData(list.iterator().next().id);
        list = postgresFactoryStorage.getFutureDataList();
        Assertions.assertEquals(1,list.size());

        Assertions.assertNotNull(postgresFactoryStorage.getFutureData(new ArrayList<>(list).get(0).id));
    }

    @Test
    public void test_history() {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());

        DataUpdate<ExampleFactoryA> update = createUpdate();
        postgresFactoryStorage.updateCurrentData(update,null);
        Collection<StoredDataMetadata> list = postgresFactoryStorage.getHistoryDataList();
        Assertions.assertEquals(2,list.size());//initial +1

        DataUpdate<ExampleFactoryA> update2 = createUpdate();
        postgresFactoryStorage.updateCurrentData(update2,null);
        list = postgresFactoryStorage.getHistoryDataList();
        Assertions.assertEquals(3,list.size());

        Assertions.assertNotNull(postgresFactoryStorage.getHistoryData(new ArrayList<>(list).get(0).id));

    }

    @Test
    public void test_getCurrentDataId() {
        ExampleFactoryA initialExampleFactoryA = createInitialExampleFactoryA();
        initialExampleFactoryA.stringAttribute.set("initial");
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, initialExampleFactoryA, createDataMigrationManager(),ObjectMapperBuilder.build());
        Assertions.assertNotNull(postgresFactoryStorage.getCurrentDataId());
        Assertions.assertEquals("initial",postgresFactoryStorage.getHistoryData(postgresFactoryStorage.getCurrentDataId()).stringAttribute.get());
    }

    @Test
    public void test_patchCurrentData()  {
        ExampleFactoryA initialExampleDataA = createInitialExampleFactoryA();
        initialExampleDataA.stringAttribute.set("123");
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        postgresFactoryStorage.getCurrentData();//init
        postgresFactoryStorage.patchCurrentData((data, metadata, objectMapper) -> {
            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
        });
        Assertions.assertEquals("qqq",postgresFactoryStorage.getCurrentData().root.stringAttribute.get());
        Assertions.assertEquals("qqq",postgresFactoryStorage.getHistoryData(postgresFactoryStorage.getCurrentDataId()).stringAttribute.get());
    }

    @Test
    @Disabled
    public void test_peformance()  {
        PostgresDataStorage<ExampleFactoryA> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(), createDataMigrationManager(),ObjectMapperBuilder.build());
        String id=postgresFactoryStorage.getCurrentData().id;

        ExampleFactoryA root = new ExampleFactoryA();
        for (int i = 0; i < 500000; i++) {
            ExampleFactoryB value = new ExampleFactoryB();
            value.stringAttribute.set("1322313sfdsfd2"+i);
            root.referenceListAttribute.add(value);
        }

        root.internal().finalise();
        DataUpdate<ExampleFactoryA> update = new DataUpdate<>(root,"user","comment","13213");;
        long start = System.currentTimeMillis();
        postgresFactoryStorage.updateCurrentData(update,null);
        System.out.println(System.currentTimeMillis()-start);
    }

    //TODO
//    @Test
//    public void test_patchAll()  {
//        ExampleFactoryA initialExampleDataA = createInitialExampleFactoryA();
//        initialExampleDataA.stringAttribute.set("123");
//        PostgresDataStorage<ExampleFactoryA,Void> postgresFactoryStorage = new PostgresDataStorage<>(postgresDatasource, createInitialExampleFactoryA(),GeneralStorageMetadataBuilder.build(), createDataMigrationManager(),ObjectMapperBuilder.build());
//        String id=postgresFactoryStorage.getCurrentData().id;
//        postgresFactoryStorage.updateCurrentData(createUpdate(),null);
//
//        postgresFactoryStorage.patchAll((data, metadata) -> {
//            ((ObjectNode) data.get("stringAttribute")).put("v", "qqq");
//        });
//        Assertions.assertEquals("qqq",postgresFactoryStorage.getHistoryData(id).stringAttribute.get());
//    }


}

