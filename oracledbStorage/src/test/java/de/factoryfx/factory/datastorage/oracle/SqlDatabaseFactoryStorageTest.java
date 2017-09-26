package de.factoryfx.factory.datastorage.postgres;

import de.factoryfx.factory.datastorage.*;
import de.factoryfx.factory.datastorage.filesystem.FileSystemFactoryStorage;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.h2.tools.Server;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class OracledbFactoryStorageTest {

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
    public void test_init_no_existing_factory() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new OracledbFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();

        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());
    }

    @Test
    public void test_init_existing_factory() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new OracledbFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;
        Assert.assertTrue(new File(folder.getRoot().getAbsolutePath()+"/currentFactory.json").exists());

        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> restored = new OracledbFactoryStorage<>(Paths.get(folder.getRoot().toURI()),null,createSerialisation());
        restored.loadInitialFactory();
        Assert.assertEquals(id,restored.getCurrentFactory().metadata.id);
    }

    @Test
    public void test_update() throws MalformedURLException {
        OracledbFactoryStorage<Void,ExampleLiveObjectA,ExampleFactoryA> fileSystemFactoryStorage = new OracledbFactoryStorage<>(Paths.get(folder.getRoot().toURI()), new ExampleFactoryA(),createSerialisation());
        fileSystemFactoryStorage.loadInitialFactory();
        String id=fileSystemFactoryStorage.getCurrentFactory().metadata.id;

        NewFactoryMetadata metadata = new NewFactoryMetadata();
        FactoryAndNewMetadata<ExampleFactoryA> update = new FactoryAndNewMetadata<>(new ExampleFactoryA(), metadata);
        fileSystemFactoryStorage.updateCurrentFactory(update,"","");
        Assert.assertNotEquals(id,fileSystemFactoryStorage.getCurrentFactory().metadata.id);
        Assert.assertEquals(2,fileSystemFactoryStorage.getHistoryFactoryList().size());

        HashSet<String> ids= new HashSet<>();
        fileSystemFactoryStorage.getHistoryFactoryList().forEach(storedFactoryMetadata -> {
            ids.add(storedFactoryMetadata.id);
        });
        Assert.assertTrue(ids.contains(id));

    }

}