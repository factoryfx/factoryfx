package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbFactoryStorageHistory<V,L,R extends FactoryBase<L,V>> {

    private final FactorySerialisationManager<R> factorySerialisationManager;
    private final Supplier<Connection> connectionSupplier;

    public OracledbFactoryStorageHistory(Supplier<Connection> connectionSupplier, FactorySerialisationManager<R> factorySerialisationManager){
        this.connectionSupplier = connectionSupplier;
        this.factorySerialisationManager= factorySerialisationManager;

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE FACTORY_HISTORY " +
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

    public R getHistoryFactory(String id) {

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_HISTORY WHERE id="+id;

                ResultSet resultSet =statement.executeQuery(sql);
                if(resultSet.next()){
                    StoredFactoryMetadata factoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(JdbcUtil.readStringToBlob(resultSet,"factoryMetadata"));
                    return  factorySerialisationManager.read(JdbcUtil.readStringToBlob(resultSet,"factory"),factoryMetadata.dataModelVersion);
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
                    StoredFactoryMetadata factoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(new String(factoryMetadataBlob.getBytes(1L, (int) factoryMetadataBlob.length())));
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
                JdbcUtil.writeStringToBlob(factorySerialisationManager.write(factoryRoot),preparedStatement,2);
                JdbcUtil.writeStringToBlob(factorySerialisationManager.writeStorageMetadata(metadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
