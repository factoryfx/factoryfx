package de.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.DataStoragePatcher;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbDataStorageHistory<R extends Data,S> {

    private final MigrationManager<R,S> migrationManager;
    private final Supplier<Connection> connectionSupplier;

    public OracledbDataStorageHistory(Supplier<Connection> connectionSupplier, MigrationManager<R,S> migrationManager){
        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;

        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
             String sql = "CREATE TABLE FACTORY_HISTORY " +
                        "(id VARCHAR(255) not NULL, " +
                        " factory BLOB, " +
                        " factoryMetadata BLOB, " +
                        " PRIMARY KEY ( id ))";

             statement.executeUpdate(sql);

        } catch (SQLException e) {
            //oracle don't know "IF NOT EXISTS"
            //workaround ignore exception
//            throw new RuntimeException(e);
        }
    }

    public R getHistoryFactory(String id) {

        try (Connection connection= connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM FACTORY_HISTORY WHERE id= ?")){
             statement.setString(1, id);
             try (ResultSet resultSet =statement.executeQuery()) {
                 if (resultSet.next()) {
                     StoredDataMetadata<S> metadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata"));
                     return migrationManager.read(JdbcUtil.readStringFromBlob(resultSet, "factory"), metadata.generalStorageMetadata, metadata.dataStorageMetadataDictionary);
                 }
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        ArrayList<StoredDataMetadata<S>> result = new ArrayList<>();
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet =statement.executeQuery("SELECT * FROM FACTORY_HISTORY")) {
             while (resultSet.next()) {
                 result.add(migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata")));
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateHistory(StoredDataMetadata<S> metadata, R factoryRoot) {
        String id=metadata.id;

        try (Connection connection= connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_HISTORY(id,factory,factoryMetadata) VALUES (?,?,? )")) {
             preparedStatement.setString(1, id);
             JdbcUtil.writeStringToBlob(migrationManager.write(factoryRoot),preparedStatement,2);
             JdbcUtil.writeStringToBlob(migrationManager.writeStorageMetadata(metadata),preparedStatement,3);
             preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void patchAll(DataStoragePatcher consumer, SimpleObjectMapper objectMapper) {

        try (Connection connection= connectionSupplier.get()){
            boolean initialAutoCommit=connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                String sql = "SELECT * FROM FACTORY_HISTORY";

                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        String dataString = JdbcUtil.readStringFromBlob(resultSet, "factory");
                        String metadataString = JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata");

                        JsonNode data = objectMapper.readTree(dataString);
                        JsonNode metadata = objectMapper.readTree(metadataString);
                        consumer.patch(data,metadata);
                        String metadataId=metadata.get("id").asText();

                        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE FACTORY_HISTORY SET factory=?,factoryMetadata=? WHERE id=?")) {
                            JdbcUtil.writeStringToBlob(objectMapper.writeTree(data),updateStatement,1);
                            JdbcUtil.writeStringToBlob(objectMapper.writeTree(metadata),updateStatement,2);
                            updateStatement.setString(3, metadataId);
                            updateStatement.executeUpdate();
                        } catch (SQLException e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                }
            } finally {
                connection.setAutoCommit(initialAutoCommit);  //connection might be from a pool better restore state
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
