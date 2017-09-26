package de.factoryfx.factory.datastorage.postgres;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.NewFactoryMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.nio.file.Files.readAllBytes;

public class SqlDatabaseFactoryStorageHistory<V,L,R extends FactoryBase<L,V>> {

    private final FactorySerialisationManager<R> factorySerialisationManager;
    private final Supplier<Connection> connectionSupplier;

    public SqlDatabaseFactoryStorageHistory(Supplier<Connection> connectionSupplier, FactorySerialisationManager<R> factorySerialisationManager){
        this.connectionSupplier = connectionSupplier;
        this.factorySerialisationManager= factorySerialisationManager;

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement();){
                String sql = "CREATE TABLE IF NOT EXISTS FACTORY_HISTORY " +
                        "(id VARCHAR(255) not NULL, " +
                        " factory BLOB, " +
                        " factoryMetadata BLOB, " +
                        " PRIMARY KEY ( id ))";

                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public R getHistoryFactory(String id) {

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_HISTORY WHERE id="+id;

                ResultSet resultSet =statement.executeQuery(sql);
                while(resultSet.next()){
                    Blob factoryMetadataBlob  = resultSet.getBlob("factoryMetadata");
                    StoredFactoryMetadata factoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(new String(factoryMetadataBlob.getBytes(1l, (int) factoryMetadataBlob.length())));

                    Blob factoryBlob  = resultSet.getBlob("factory");
                    return  factorySerialisationManager.read(new String(factoryBlob.getBytes(1l, (int) factoryBlob.length())),factoryMetadata.dataModelVersion);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        ArrayList<StoredFactoryMetadata> result = new ArrayList<>();
        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_HISTORY";

                ResultSet resultSet =statement.executeQuery(sql);
                while(resultSet.next()){
                    Blob factoryMetadataBlob  = resultSet.getBlob("factoryMetadata");
                    StoredFactoryMetadata factoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(new String(factoryMetadataBlob.getBytes(1l, (int) factoryMetadataBlob.length())));
                    result.add(factoryMetadata);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateHistory(StoredFactoryMetadata metadata, R factoryRoot) {
        String id=metadata.id;

        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_HISTORY(id,factory,factoryMetadata) VALUES (?,?,? )")){
                preparedStatement.setString(1, id);
                writeStringToBlob(factorySerialisationManager.write(factoryRoot),preparedStatement,2);
                writeStringToBlob(factorySerialisationManager.writeStorageMetadata(metadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeStringToBlob(String value, PreparedStatement preparedStatement,int index){
        try {
            preparedStatement.setBinaryStream(index, new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)), (int)value.length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
