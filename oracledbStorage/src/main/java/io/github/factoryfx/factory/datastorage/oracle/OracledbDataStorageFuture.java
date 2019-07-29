package io.github.factoryfx.factory.datastorage.oracle;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbDataStorageFuture<R extends FactoryBase<?,R>> {

    private final MigrationManager<R> migrationManager;
    private final Supplier<Connection> connectionSupplier;

    public OracledbDataStorageFuture(Supplier<Connection> connectionSupplier, MigrationManager<R> migrationManager){
        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;

        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE FACTORY_FUTURE " +
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

    public R getFutureFactory(String id) {

        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_FUTURE WHERE id='"+id+"'";

            try (ResultSet resultSet =statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    ScheduledUpdateMetadata metadata = migrationManager.readScheduledFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata"));
                    return migrationManager.read(JdbcUtil.readStringFromBlob(resultSet, "factory"),metadata.dataStorageMetadataDictionary);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<ScheduledUpdateMetadata> getFutureFactoryList() {
        ArrayList<ScheduledUpdateMetadata> result = new ArrayList<>();
        try (Connection connection= connectionSupplier.get();
            Statement statement = connection.createStatement();
             ResultSet resultSet =statement.executeQuery("SELECT * FROM FACTORY_FUTURE")
            ){
            while (resultSet.next()) {
                result.add(migrationManager.readScheduledFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet, "factoryMetadata")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void addFuture(ScheduledUpdateMetadata metadata, R factoryRoot) {
        String id=metadata.id;

        try (Connection connection= connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_FUTURE(id,factory,factoryMetadata) VALUES (?,?,? )")){
             preparedStatement.setString(1, id);
             JdbcUtil.writeStringToBlob(migrationManager.write(factoryRoot),preparedStatement,2);
             JdbcUtil.writeStringToBlob(migrationManager.writeScheduledUpdateMetadata(metadata),preparedStatement,3);
             preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteFutureFactory(String id) {
        try (Connection connection= connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM FACTORY_FUTURE WHERE id = ?")){
             preparedStatement.setString(1, id);
             preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
