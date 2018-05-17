package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.StoredDataMetadata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbDataStorageHistory<R extends Data,S> {

    private final DataSerialisationManager<R,S> dataSerialisationManager;
    private final Supplier<Connection> connectionSupplier;

    public OracledbDataStorageHistory(Supplier<Connection> connectionSupplier, DataSerialisationManager<R,S> dataSerialisationManager){
        this.connectionSupplier = connectionSupplier;
        this.dataSerialisationManager = dataSerialisationManager;

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
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM FACTORY_HISTORY WHERE id= ?")){
                statement.setString(1, id);
                ResultSet resultSet =statement.executeQuery();
                if(resultSet.next()){
                    StoredDataMetadata factoryMetadata = dataSerialisationManager.readStoredFactoryMetadata(JdbcUtil.readStringToBlob(resultSet,"factoryMetadata"));
                    return  dataSerialisationManager.read(JdbcUtil.readStringToBlob(resultSet,"factory"),factoryMetadata.dataModelVersion);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        ArrayList<StoredDataMetadata<S>> result = new ArrayList<>();
        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_HISTORY";
                try (ResultSet resultSet =statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        result.add(dataSerialisationManager.readStoredFactoryMetadata(JdbcUtil.readStringToBlob(resultSet, "factoryMetadata")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateHistory(StoredDataMetadata<S> metadata, R factoryRoot) {
        String id=metadata.id;

        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_HISTORY(id,factory,factoryMetadata) VALUES (?,?,? )")){
                preparedStatement.setString(1, id);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.write(factoryRoot),preparedStatement,2);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.writeStorageMetadata(metadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
