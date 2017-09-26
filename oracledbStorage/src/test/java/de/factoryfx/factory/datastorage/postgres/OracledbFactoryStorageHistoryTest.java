package de.factoryfx.factory.datastorage.postgres;

import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.JacksonDeSerialisation;
import de.factoryfx.factory.datastorage.JacksonSerialisation;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.h2.tools.Server;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class SqlDatabaseFactoryStorageHistoryTest {
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    Server server;
    Supplier<Connection> connectionSupplier;
    @Before
    public void setup(){
        try {
            server = Server.createTcpServer("-tcpAllowOthers").start();
            Class.forName("org.h2.Driver");
            connectionSupplier= () -> {
                try {
                    return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            };
//            System.out.println("Connection Established: "
//                    + conn.getMetaData().getDatabaseProductName() + "/" + conn.getCatalog());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @After
    public void shutdown() throws SQLException {
        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement= connection.prepareStatement("DROP ALL OBJECTS")){
                preparedStatement.execute();
            }
        }


        server.stop();
    }



    private FactorySerialisationManager<ExampleFactoryA> createSerialisation(){
        int dataModelVersion = 1;
        return new FactorySerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleFactoryA.class, dataModelVersion), Collections.emptyList(),1);
    }


    @Test
    public void test_empty() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        Assert.assertTrue(oracledbFactoryStorageHistory.getHistoryFactoryList().isEmpty());
    }

    @Test
    public void test_add() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());

        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_multi_add() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            metadata.id = UUID.randomUUID().toString();
            oracledbFactoryStorageHistory.updateHistory(metadata, new ExampleFactoryA());
        }

        assertEquals(3, oracledbFactoryStorageHistory.getHistoryFactoryList().size());
    }

    @Test
    public void test_restore() throws MalformedURLException {
        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> oracledbFactoryStorageHistory = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());

        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id= UUID.randomUUID().toString();
        oracledbFactoryStorageHistory.updateHistory(metadata,new ExampleFactoryA());
        assertEquals(1, oracledbFactoryStorageHistory.getHistoryFactoryList().size());

        OracledbFactoryStorageHistory<Void,ExampleLiveObjectA,ExampleFactoryA> restored = new OracledbFactoryStorageHistory<>(connectionSupplier,createSerialisation());
        Assert.assertEquals(1,restored.getHistoryFactoryList().size());
    }
}