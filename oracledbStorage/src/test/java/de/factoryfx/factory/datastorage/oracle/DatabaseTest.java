package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.JacksonDeSerialisation;
import de.factoryfx.factory.datastorage.JacksonSerialisation;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
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


    protected FactorySerialisationManager<ExampleFactoryA> createSerialisation(){
        int dataModelVersion = 1;
        return new FactorySerialisationManager<>(new JacksonSerialisation<>(dataModelVersion),new JacksonDeSerialisation<>(ExampleFactoryA.class, dataModelVersion), Collections.emptyList(),1);
    }

}
