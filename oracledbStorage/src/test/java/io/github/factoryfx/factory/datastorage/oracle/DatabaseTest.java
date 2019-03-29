package io.github.factoryfx.factory.datastorage.oracle;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;

public class DatabaseTest {

    Server server;
    Supplier<Connection> connectionSupplier;
    @BeforeEach
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
    @AfterEach
    public void shutdown() throws SQLException {
        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement= connection.prepareStatement("DROP ALL OBJECTS")){
                preparedStatement.execute();
            }
        }
        server.stop();
    }


    protected MigrationManager<ExampleFactoryA,Void> createMigrationManager(){
        return new MigrationManager<>(ExampleFactoryA.class, ObjectMapperBuilder.build(), (root, d) -> { });
    }

}
