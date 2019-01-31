package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
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
            //oracle don't know IF NOT EXISTS
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
                     StoredDataMetadata<S> factoryMetadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata"));
                     return migrationManager.read(JdbcUtil.readStringFromBlob(resultSet, "factory"), factoryMetadata);
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


}
