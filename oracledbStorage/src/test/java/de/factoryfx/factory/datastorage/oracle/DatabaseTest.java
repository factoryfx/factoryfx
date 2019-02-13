package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

public class DatabaseTest {

    Server server;
    Supplier<Connection> connectionSupplier;
    @Before
    public void setup(){
        try {
            server = Server.createTcpServer("-tcpAllowOthers").start();
            Class.forName("org.h2.Driver");
            connectionSupplier= () -> {
                try {
                    return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle", "sa", "");
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


    protected MigrationManager<ExampleFactoryA,Void> createMigrationManager(){
        return new MigrationManager<>(ExampleFactoryA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager(), ObjectMapperBuilder.build());
    }

}
