package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.MigrationManager;

import java.sql.*;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final OracledbDataStorageHistory<R,S> oracledbDataStorageHistory;
    private final OracledbDataStorageFuture<R,S> oracledbDataStorageFuture;
    private final DataAndStoredMetadata<R,S> initialFactory;
    private final MigrationManager<R,S> migrationManager;
    private final Supplier< Connection > connectionSupplier;

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, DataAndStoredMetadata<R,S> initialFactory, MigrationManager<R,S> migrationManager, OracledbDataStorageHistory<R,S> oracledbDataStorageHistory, OracledbDataStorageFuture<R,S> oracledbDataStorageFuture){
        this.initialFactory=initialFactory;
        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;
        this.oracledbDataStorageHistory = oracledbDataStorageHistory;
        this.oracledbDataStorageFuture = oracledbDataStorageFuture;

        try (Connection connection= connectionSupplier.get();
         Statement statement = connection.createStatement()){
            String sql = "CREATE TABLE FACTORY_CURRENT " +
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

    public OracledbDataStorage(Supplier< Connection > connectionSupplier, DataAndStoredMetadata<R,S> defaultFactory, MigrationManager<R,S> migrationManager){
        this(connectionSupplier,defaultFactory, migrationManager,new OracledbDataStorageHistory<>(connectionSupplier, migrationManager),
                new OracledbDataStorageFuture<>(connectionSupplier, migrationManager));
    }

    @Override
    public R getHistoryFactory(String id) {
        return oracledbDataStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return oracledbDataStorageHistory.getHistoryFactoryList();
    }

    @Override
    public DataAndId<R> getCurrentFactory() {
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    StoredDataMetadata<S> factoryMetadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet,"factoryMetadata"));
                    return new DataAndId<>(migrationManager.read(JdbcUtil.readStringFromBlob(resultSet,"factory"),factoryMetadata),factoryMetadata.id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        updateCurrentFactory(initialFactory);
        return new DataAndId<>(initialFactory.root,initialFactory.metadata.id);
    }

    @Override
    public void updateCurrentFactory(DataAndStoredMetadata<R,S> update) {
        try (Connection connection= connectionSupplier.get();
             PreparedStatement truncate = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")
        ) {
            truncate.execute();
            preparedStatement.setString(1, update.metadata.id);
            JdbcUtil.writeStringToBlob(migrationManager.write(update.root),preparedStatement,2);
            JdbcUtil.writeStringToBlob(migrationManager.writeStorageMetadata(update.metadata),preparedStatement,3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracledbDataStorageHistory.updateHistory(update.metadata,update.root);
    }


    @Override
    public Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        return oracledbDataStorageFuture.getFutureFactoryList();
    }

    @Override
    public void deleteFutureFactory(String id) {
        oracledbDataStorageFuture.deleteFutureFactory(id);
    }

    public R getFutureFactory(String id) {
        return oracledbDataStorageFuture.getFutureFactory(id);
    }

    @Override
    public void addFutureFactory(DataAndScheduledMetadata<R,S> futureFactory) {
        oracledbDataStorageFuture.addFuture(futureFactory.metadata,futureFactory.root);
    }



}
