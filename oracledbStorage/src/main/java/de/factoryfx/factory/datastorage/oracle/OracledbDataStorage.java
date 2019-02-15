package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.MigrationManager;

import java.sql.*;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class OracledbDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final OracledbDataStorageHistory<R,S> oracledbDataStorageHistory;
    private final OracledbDataStorageFuture<R,S> oracledbDataStorageFuture;
    private final R initialData;
    private final MigrationManager<R,S> migrationManager;
    private final Supplier< Connection > connectionSupplier;
    private final GeneralStorageMetadata generalStorageMetadata;

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, R initialDataParam, GeneralStorageMetadata generalStorageMetadata,  MigrationManager<R,S> migrationManager, OracledbDataStorageHistory<R,S> oracledbDataStorageHistory, OracledbDataStorageFuture<R,S> oracledbDataStorageFuture){
        this.initialData = initialDataParam;

        this.generalStorageMetadata=generalStorageMetadata;
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

    public OracledbDataStorage(Supplier< Connection > connectionSupplier, R initialDataParam, GeneralStorageMetadata generalStorageMetadata, MigrationManager<R,S> migrationManager){
        this(connectionSupplier,initialDataParam,generalStorageMetadata, migrationManager,new OracledbDataStorageHistory<>(connectionSupplier, migrationManager),
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
                    return new DataAndId<>(migrationManager.read(JdbcUtil.readStringFromBlob(resultSet,"factory"),factoryMetadata.generalStorageMetadata,factoryMetadata.dataStorageMetadataDictionary),factoryMetadata.id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StoredDataMetadata<S> metadata=new StoredDataMetadata<>(
                UUID.randomUUID().toString(),
                "System",
                "initial factory",
                UUID.randomUUID().toString(),
                null, generalStorageMetadata,
                initialData.internal().createDataStorageMetadataDictionaryFromRoot()
        );

        update(initialData, metadata);
        return new DataAndId<>(initialData, metadata.id);
    }

    @Override
    public void updateCurrentFactory(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata =update.createUpdateStoredDataMetadata(changeSummary,generalStorageMetadata);
        update(update.root, metadata);
    }

    private void update(R update, StoredDataMetadata<S> metadata) {
        try (Connection connection= connectionSupplier.get();
             PreparedStatement truncate = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")
        ) {
            truncate.execute();
            preparedStatement.setString(1, metadata.id);
            JdbcUtil.writeStringToBlob(migrationManager.write(update),preparedStatement,2);
            JdbcUtil.writeStringToBlob(migrationManager.writeStorageMetadata(metadata),preparedStatement,3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracledbDataStorageHistory.updateHistory(metadata,update);
    }


    @Override
    public Collection<ScheduledUpdateMetadata> getFutureFactoryList() {
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
    public void addFutureFactory(ScheduledUpdate<R> futureFactory) {
        ScheduledUpdateMetadata scheduledUpdateMetadata =new ScheduledUpdateMetadata(
                UUID.randomUUID().toString(),
                futureFactory.user,
                futureFactory.comment,
                futureFactory.scheduled,
                generalStorageMetadata,
                futureFactory.root.internal().createDataStorageMetadataDictionaryFromRoot()
        );

        oracledbDataStorageFuture.addFuture(scheduledUpdateMetadata,futureFactory.root);
    }



}
