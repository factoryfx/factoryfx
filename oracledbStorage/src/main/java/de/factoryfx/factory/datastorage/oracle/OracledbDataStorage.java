package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class OracledbDataStorage<R extends Data> implements DataStorage<R> {
    private final OracledbFactoryStorageHistory<R> oracledbFactoryStorageHistory;
    private final OracledbFactoryStorageFuture<R> oracledbFactoryStorageFuture;
    private final R initialFactory;
    private final DataSerialisationManager<R> dataSerialisationManager;
    private final Supplier< Connection > connectionSupplier;

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, R defaultFactory, DataSerialisationManager<R> dataSerialisationManager, OracledbFactoryStorageHistory<R> oracledbFactoryStorageHistory, OracledbFactoryStorageFuture<R> oracledbFactoryStorageFuture){
        this.initialFactory=defaultFactory;
        this.connectionSupplier = connectionSupplier;
        this.dataSerialisationManager = dataSerialisationManager;
        this.oracledbFactoryStorageHistory = oracledbFactoryStorageHistory;
        this.oracledbFactoryStorageFuture = oracledbFactoryStorageFuture;

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE FACTORY_CURRENT " +
                        "(id VARCHAR(255) not NULL, " +
                        " factory BLOB, " +
                        " factoryMetadata BLOB, " +
                        " PRIMARY KEY ( id ))";

                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            //oracle don't know IF NOT EXISTS
            //workaround ignore exception
//            throw new RuntimeException(e);
        }
    }

    public OracledbDataStorage(Supplier< Connection > connectionSupplier, R defaultFactory, DataSerialisationManager<R> dataSerialisationManager){
        this(connectionSupplier,defaultFactory, dataSerialisationManager,new OracledbFactoryStorageHistory<>(connectionSupplier, dataSerialisationManager),
                new OracledbFactoryStorageFuture<>(connectionSupplier, dataSerialisationManager));
    }

    @Override
    public R getHistoryFactory(String id) {
        return oracledbFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return oracledbFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public DataAndStoredMetadata<R> getCurrentFactory() {
        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_CURRENT";

                ResultSet resultSet =statement.executeQuery(sql);
                if(resultSet.next()){
                    StoredDataMetadata factoryMetadata = dataSerialisationManager.readStoredFactoryMetadata(JdbcUtil.readStringToBlob(resultSet,"factoryMetadata"));
                    return new DataAndStoredMetadata<>(dataSerialisationManager.read(JdbcUtil.readStringToBlob(resultSet,"factory"),factoryMetadata.dataModelVersion),factoryMetadata);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment) {
        final StoredDataMetadata storedDataMetadata = new StoredDataMetadata();
        storedDataMetadata.creationTime= LocalDateTime.now();
        storedDataMetadata.id= UUID.randomUUID().toString();
        storedDataMetadata.user=user;
        storedDataMetadata.comment=comment;
        storedDataMetadata.baseVersionId=update.metadata.baseVersionId;
        storedDataMetadata.dataModelVersion=update.metadata.dataModelVersion;

        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT")){
                preparedStatement.execute();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")){
                preparedStatement.setString(1, storedDataMetadata.id);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.write(update.root),preparedStatement,2);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.writeStorageMetadata(storedDataMetadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracledbFactoryStorageHistory.updateHistory(storedDataMetadata,update.root);
    }

    @Override
    public DataAndNewMetadata<R> getPrepareNewFactory(){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        dataSerialisationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
    }


    @Override
    public void loadInitialFactory() {
        DataAndStoredMetadata<R> currentFactory= getCurrentFactory();
        if (currentFactory==null){
            NewDataMetadata metadata = new NewDataMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            DataAndNewMetadata<R> initialFactoryAndStorageMetadata = new DataAndNewMetadata<>(initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata,"System","initial factory");
        }
    }

    @Override
    public Collection<ScheduledDataMetadata> getFutureFactoryList() {
        return oracledbFactoryStorageFuture.getFutureFactoryList();
    }

    @Override
    public void deleteFutureFactory(String id) {
        oracledbFactoryStorageFuture.deleteFutureFactory(id);
    }

    public R getFutureFactory(String id) {
        return oracledbFactoryStorageFuture.getFutureFactory(id);
    }

    @Override
    public void addFutureFactory(DataAndNewMetadata<R> update, String user, String comment, LocalDateTime scheduled) {
        final ScheduledDataMetadata storedFactoryMetadata = new ScheduledDataMetadata();
        storedFactoryMetadata.creationTime=LocalDateTime.now();
        storedFactoryMetadata.id= UUID.randomUUID().toString();
        storedFactoryMetadata.user=user;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=update.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=update.metadata.dataModelVersion;
        storedFactoryMetadata.scheduled = scheduled;

        final DataAndScheduledMetadata<R> updateData = new DataAndScheduledMetadata<>(update.root, storedFactoryMetadata);
        oracledbFactoryStorageFuture.addFuture(storedFactoryMetadata,update.root);
    }



}
