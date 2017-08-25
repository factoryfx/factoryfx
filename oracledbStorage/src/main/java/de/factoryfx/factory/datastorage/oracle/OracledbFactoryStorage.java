package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.*;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class OracledbFactoryStorage<V,L,R extends FactoryBase<L,V>> implements FactoryStorage<V,L, R> {
    private final OracledbFactoryStorageHistory<V,L, R> oracledbFactoryStorageHistory;
    private final R initialFactory;
    private final FactorySerialisationManager<R> factorySerialisationManager;
    private final Supplier< Connection > connectionSupplier;

    public OracledbFactoryStorage(Supplier< Connection > connectionSupplier, R defaultFactory, FactorySerialisationManager<R> factorySerialisationManager, OracledbFactoryStorageHistory<V,L, R> oracledbFactoryStorageHistory){
        this.initialFactory=defaultFactory;
        this.connectionSupplier = connectionSupplier;
        this.factorySerialisationManager= factorySerialisationManager;
        this.oracledbFactoryStorageHistory = oracledbFactoryStorageHistory;

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

    public OracledbFactoryStorage(Supplier< Connection > connectionSupplier, R defaultFactory, FactorySerialisationManager<R> factorySerialisationManager){
        this(connectionSupplier,defaultFactory,factorySerialisationManager,new OracledbFactoryStorageHistory<>(connectionSupplier,factorySerialisationManager));
    }

    @Override
    public R getHistoryFactory(String id) {
        return oracledbFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return oracledbFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public FactoryAndStoredMetadata<R> getCurrentFactory() {
        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_CURRENT";

                ResultSet resultSet =statement.executeQuery(sql);
                if(resultSet.next()){
                    StoredFactoryMetadata factoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(JdbcUtil.readStringToBlob(resultSet,"factoryMetadata"));
                    return new FactoryAndStoredMetadata<>(factorySerialisationManager.read(JdbcUtil.readStringToBlob(resultSet,"factory"),factoryMetadata.dataModelVersion),factoryMetadata);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateCurrentFactory(FactoryAndNewMetadata<R> update, String user, String comment) {
        final StoredFactoryMetadata storedFactoryMetadata = new StoredFactoryMetadata();
        storedFactoryMetadata.creationTime= LocalDateTime.now();
        storedFactoryMetadata.id= UUID.randomUUID().toString();
        storedFactoryMetadata.user=user;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=update.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=update.metadata.dataModelVersion;

        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE FACTORY_CURRENT")){
                preparedStatement.execute();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_CURRENT(id,factory,factoryMetadata) VALUES (?,?,? )")){
                preparedStatement.setString(1, storedFactoryMetadata.id);
                JdbcUtil.writeStringToBlob(factorySerialisationManager.write(update.root),preparedStatement,2);
                JdbcUtil.writeStringToBlob(factorySerialisationManager.writeStorageMetadata(storedFactoryMetadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        oracledbFactoryStorageHistory.updateHistory(storedFactoryMetadata,update.root);
    }

    @Override
    public FactoryAndNewMetadata<R> getPrepareNewFactory(){
        NewFactoryMetadata metadata = new NewFactoryMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        factorySerialisationManager.prepareNewFactoryMetadata(metadata);
        return new FactoryAndNewMetadata<>(getCurrentFactory().root,metadata);
    }


    @Override
    public void loadInitialFactory() {
        FactoryAndStoredMetadata<R> currentFactory= getCurrentFactory();
        if (currentFactory==null){
            NewFactoryMetadata metadata = new NewFactoryMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            FactoryAndNewMetadata<R> initialFactoryAndStorageMetadata = new FactoryAndNewMetadata<>(initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata,"System","initial factory");
        }
    }




}
