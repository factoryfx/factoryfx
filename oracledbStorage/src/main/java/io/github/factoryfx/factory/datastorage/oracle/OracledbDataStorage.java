package io.github.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.*;

import java.sql.*;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class OracledbDataStorage<R extends FactoryBase<?,R>> implements DataStorage<R> {
    private final OracledbDataStorageHistory<R> oracledbDataStorageHistory;
    private final OracledbDataStorageFuture<R> oracledbDataStorageFuture;
    private final R initialData;
    private final MigrationManager<R> migrationManager;
    private final Supplier< Connection > connectionSupplier;
    private final SimpleObjectMapper objectMapper;

    public OracledbDataStorage(Supplier<Connection> connectionSupplier, R initialDataParam,  MigrationManager<R> migrationManager, OracledbDataStorageHistory<R> oracledbDataStorageHistory, OracledbDataStorageFuture<R> oracledbDataStorageFuture, SimpleObjectMapper objectMapper){
        this.initialData = initialDataParam;

        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;
        this.oracledbDataStorageHistory = oracledbDataStorageHistory;
        this.oracledbDataStorageFuture = oracledbDataStorageFuture;
        this.objectMapper = objectMapper;

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

    public OracledbDataStorage(Supplier< Connection > connectionSupplier, R initialDataParam, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper){
        this(connectionSupplier,initialDataParam, migrationManager,new OracledbDataStorageHistory<>(connectionSupplier, migrationManager),
                new OracledbDataStorageFuture<>(connectionSupplier, migrationManager),objectMapper);
    }

    @Override
    public R getHistoryData(String id) {
        return oracledbDataStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryDataList() {
        return oracledbDataStorageHistory.getHistoryFactoryList();
    }

    @Override
    public DataAndId<R> getCurrentData() {
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    StoredDataMetadata factoryMetadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readStringFromBlob(resultSet,"factoryMetadata"));
                    return new DataAndId<>(migrationManager.read(JdbcUtil.readStringFromBlob(resultSet,"factory"),factoryMetadata.dataStorageMetadataDictionary),factoryMetadata.id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StoredDataMetadata metadata = initCurrentData();
        return new DataAndId<>(initialData, metadata.id);
    }

    private StoredDataMetadata initCurrentData() {
        StoredDataMetadata metadata = new StoredDataMetadata(UUID.randomUUID().toString(), "System", "initial factory", UUID.randomUUID().toString(), null, initialData.internal().createDataStorageMetadataDictionaryFromRoot(), null);

        update(initialData, metadata);
        return metadata;
    }

    @Override
    public String getCurrentDataId() {
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    return resultSet.getString("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StoredDataMetadata metadata = initCurrentData();
        return metadata.id;
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, UpdateSummary changeSummary) {
        StoredDataMetadata metadata =update.createUpdateStoredDataMetadata(changeSummary,getCurrentDataId());
        update(update.root, metadata);
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
        patchCurrentData(consumer);
        oracledbDataStorageHistory.patchAll(consumer,objectMapper);
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        String dataString=null;
        String metadataString=null;
        try (Connection connection= connectionSupplier.get();
             Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM FACTORY_CURRENT";

            try (ResultSet resultSet =statement.executeQuery(sql)){
                if(resultSet.next()){
                    dataString= JdbcUtil.readStringFromBlob(resultSet,"factory");
                    metadataString= JdbcUtil.readStringFromBlob(resultSet,"factoryMetadata");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JsonNode data = objectMapper.readTree(dataString);
        JsonNode metadata = objectMapper.readTree(metadataString);
        consumer.patch((ObjectNode) data,metadata,objectMapper);

        String metadataId=metadata.get("id").asText();

        try (Connection connection= connectionSupplier.get();
             PreparedStatement truncate = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")
        ) {
            truncate.execute();
            preparedStatement.setString(1, metadataId);
            JdbcUtil.writeStringToBlob(objectMapper.writeTree(data),preparedStatement,2);
            JdbcUtil.writeStringToBlob(objectMapper.writeTree(metadata),preparedStatement,3);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(R update, StoredDataMetadata metadata) {
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
    public Collection<ScheduledUpdateMetadata> getFutureDataList() {
        return oracledbDataStorageFuture.getFutureFactoryList();
    }

    @Override
    public void deleteFutureData(String id) {
        oracledbDataStorageFuture.deleteFutureFactory(id);
    }

    public R getFutureData(String id) {
        return oracledbDataStorageFuture.getFutureFactory(id);
    }

    @Override
    public void addFutureData(ScheduledUpdate<R> futureFactory) {
        ScheduledUpdateMetadata scheduledUpdateMetadata =new ScheduledUpdateMetadata(
                UUID.randomUUID().toString(),
                futureFactory.user,
                futureFactory.comment,
                futureFactory.scheduled,
                futureFactory.root.internal().createDataStorageMetadataDictionaryFromRoot()
        );

        oracledbDataStorageFuture.addFuture(scheduledUpdateMetadata,futureFactory.root);
    }



}
