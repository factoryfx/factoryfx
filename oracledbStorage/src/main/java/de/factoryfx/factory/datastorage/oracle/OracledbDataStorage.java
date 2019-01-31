package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.MigrationManager;

import java.sql.*;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class OracledbDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final OracledbDataStorageHistory<R,S> oracledbDataStorageHistory;
    private final OracledbDataStorageFuture<R,S> oracledbDataStorageFuture;
    private final R initialFactory;
    private final MigrationManager<R,S> migrationManager;
    private final Supplier< Connection > connectionSupplier;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, R defaultFactory, MigrationManager<R,S> migrationManager, OracledbDataStorageHistory<R,S> oracledbDataStorageHistory, OracledbDataStorageFuture<R,S> oracledbDataStorageFuture, ChangeSummaryCreator<R,S> changeSummaryCreator ){
        this.initialFactory=defaultFactory;
        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;
        this.oracledbDataStorageHistory = oracledbDataStorageHistory;
        this.oracledbDataStorageFuture = oracledbDataStorageFuture;
        this.changeSummaryCreator=changeSummaryCreator;

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

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, R defaultFactory, MigrationManager<R,S> migrationManager, OracledbDataStorageHistory<R,S> oracledbDataStorageHistory, OracledbDataStorageFuture<R,S> oracledbDataStorageFuture){
        this(
            connectionSupplier,
            defaultFactory, migrationManager, oracledbDataStorageHistory, oracledbDataStorageFuture,
            (d)->null
        );

    }
    public OracledbDataStorage(Supplier< Connection > connectionSupplier, R defaultFactory, MigrationManager<R,S> migrationManager){
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
    public DataAndStoredMetadata<R,S> getCurrentFactory() {
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    StoredDataMetadata<S> factoryMetadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet,"factoryMetadata"));
                    return new DataAndStoredMetadata<>(migrationManager.read(JdbcUtil.readStringFromBlob(resultSet,"factory"),factoryMetadata),factoryMetadata);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getCurrentFactoryStorageId() {
        //TODO cache id to improve performance
        try (Connection connection= connectionSupplier.get(); Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    StoredDataMetadata<S> factoryMetadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet,"factoryMetadata"));
                    return factoryMetadata.id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiffInfo) {
        S changeSummary = null;
        if (mergeDiffInfo!=null){
            this.changeSummaryCreator.createChangeSummary(mergeDiffInfo);
        }
        final StoredDataMetadata<S> storedDataMetadata = migrationManager.createStoredDataMetadata(user, comment, update.metadata.baseVersionId, changeSummary);

        try (Connection connection= connectionSupplier.get();
             PreparedStatement truncate = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")
        ) {
            truncate.execute();
            preparedStatement.setString(1, storedDataMetadata.id);
            JdbcUtil.writeStringToBlob(migrationManager.write(update.root),preparedStatement,2);
            JdbcUtil.writeStringToBlob(migrationManager.writeStorageMetadata(storedDataMetadata),preparedStatement,3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracledbDataStorageHistory.updateHistory(storedDataMetadata,update.root);
    }

    @Override
    public DataAndNewMetadata<R> prepareNewFactory(String currentFactoryStorageId, R currentFactoryCopy){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=currentFactoryStorageId;
        migrationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(currentFactoryCopy,metadata);
    }


    @Override
    public void loadInitialFactory() {
        DataAndStoredMetadata<R,S> currentFactory= getCurrentFactory();
        if (currentFactory==null){
            NewDataMetadata metadata = new NewDataMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            DataAndNewMetadata<R> initialFactoryAndStorageMetadata = new DataAndNewMetadata<>(initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata,"System","initial factory",null);
        }
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
    public ScheduledDataMetadata<S> addFutureFactory(R futureFactory, NewScheduledDataMetadata futureFactoryMetadata, String user, String comment, MergeDiffInfo<R> mergeDiffInfo) {
        final ScheduledDataMetadata<S> storedFactoryMetadata = migrationManager.createScheduledDataMetadata(user, comment, futureFactoryMetadata.newDataMetadata.baseVersionId, this.changeSummaryCreator.createFutureChangeSummary(mergeDiffInfo), futureFactoryMetadata.scheduled);
        oracledbDataStorageFuture.addFuture(storedFactoryMetadata,futureFactory);
        return storedFactoryMetadata;
    }



}
