package io.github.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.OutputStyle;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.DataStoragePatcher;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class OracledbDataStorageHistory<R extends FactoryBase<?, R>> {

    private final MigrationManager<R> migrationManager;
    private final Supplier<Connection> connectionSupplier;
    private final SimpleObjectMapper objectMapper;

    public OracledbDataStorageHistory(Supplier<Connection> connectionSupplier, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper, boolean withHistoryCompression) {
        this.connectionSupplier = connectionSupplier;
        this.migrationManager = migrationManager;
        this.objectMapper = objectMapper;

        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement()) {

            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet rs = metaData.getTables(null, null, "FACTORY_HISTORY", new String[]{"TABLE"})) {
                if (!rs.next()) {
                    String sql = "CREATE TABLE FACTORY_HISTORY " +
                            "(id VARCHAR(255) not NULL, " +
                            " factory BLOB, " +
                            " factoryMetadata BLOB, " +
                            " PRIMARY KEY ( id ))";

                    statement.executeUpdate(sql);

                }
            }

            if (isHistoryCompressible(connection)) {
                if (withHistoryCompression != isHistoryCompressionAlreadyActivated(connection)) {

                    String sql = withHistoryCompression
                            ? "ALTER TABLE FACTORY_HISTORY MOVE LOB(FACTORY) STORE AS SECUREFILE (COMPRESS HIGH)"
                            : "ALTER TABLE FACTORY_HISTORY MOVE LOB(FACTORY) STORE AS SECUREFILE (NOCOMPRESS)";

                    statement.executeUpdate(sql);
                    rebuildIndexes(connection);
                }

            } else if (withHistoryCompression) {
                throw new RuntimeException("cannot use withHistoryCompression flag for this database table");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isHistoryCompressible(Connection connection) {
        if (!JdbcUtil.isOracleWithCompressionSupport(connection)) {
            return false;
        }

        try {
            String sql = """
                    SELECT 1
                    FROM user_lobs
                    WHERE table_name = 'FACTORY_HISTORY' and column_name = 'FACTORY' and securefile = 'YES'""";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isHistoryCompressionAlreadyActivated(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM user_lobs WHERE table_name = 'FACTORY_HISTORY' and column_name = 'FACTORY' and COMPRESSION != 'NO'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    private static void rebuildIndexes(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT index_name FROM user_indexes WHERE table_name = 'FACTORY_HISTORY' AND status != 'VALID'")) {

            while (rs.next()) {
                String indexName = rs.getString("index_name");
                try (Statement rebuildStmt = connection.createStatement()) {
                    rebuildStmt.execute("ALTER INDEX " + indexName + " REBUILD");
                }
            }
        }
    }

    public R getHistoryFactory(String id) {

        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM FACTORY_HISTORY WHERE id= ?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    StoredDataMetadata metadata = migrationManager.readStoredFactoryMetadata(JdbcUtil.readTreeFromBlob(resultSet, "factoryMetadata", objectMapper));
                    return migrationManager.read(JdbcUtil.readTreeFromBlob(resultSet, "factory", objectMapper), metadata.dataStorageMetadataDictionary);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        ArrayList<StoredDataMetadata> result = new ArrayList<>();
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM FACTORY_HISTORY")) {
            while (resultSet.next()) {
                result.add(migrationManager.readStoredFactoryMetadata(JdbcUtil.readTreeFromBlob(resultSet, "factoryMetadata", objectMapper)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void updateHistory(StoredDataMetadata metadata, R factoryRoot) {
        String id = metadata.id;
        List<Blob> allocatedBlobs = new ArrayList<>();
        try (Connection connection = connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_HISTORY(id,factory,factoryMetadata) VALUES (?,?,? )")) {
            preparedStatement.setString(1, id);
            JdbcUtil.writeToBlob(preparedStatement, 2, out -> objectMapper.writeValue(out, factoryRoot, OutputStyle.COMPACT), allocatedBlobs);
            JdbcUtil.writeToBlob(preparedStatement, 3, out -> objectMapper.writeValue(out, metadata, OutputStyle.COMPACT), allocatedBlobs);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.freeBlobs(allocatedBlobs);
        }
    }


    public void patchAll(DataStoragePatcher consumer) {

        try (Connection connection = connectionSupplier.get()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                String sql = "SELECT * FROM FACTORY_HISTORY";

                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        JsonNode data = JdbcUtil.readTreeFromBlob(resultSet, "factory", objectMapper);
                        JsonNode metadata = JdbcUtil.readTreeFromBlob(resultSet, "factoryMetadata", objectMapper);

                        consumer.patch((ObjectNode) data, metadata, objectMapper);
                        String metadataId = metadata.get("id").asText();
                        List<Blob> allocatedBlobs = new ArrayList<>();

                        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE FACTORY_HISTORY SET factory=?,factoryMetadata=? WHERE id=?")) {
                            JdbcUtil.writeToBlob(updateStatement, 1, out -> objectMapper.writeValue(out, data, OutputStyle.COMPACT), allocatedBlobs);
                            JdbcUtil.writeToBlob(updateStatement, 2, out -> objectMapper.writeValue(out, metadata, OutputStyle.COMPACT), allocatedBlobs);
                            updateStatement.setString(3, metadataId);
                            updateStatement.executeUpdate();
                        } catch (SQLException e1) {
                            throw new RuntimeException(e1);
                        } finally {
                            JdbcUtil.freeBlobs(allocatedBlobs);
                        }
                    }
                }
                connection.commit();
            } finally {
                connection.setAutoCommit(initialAutoCommit);  //connection might be from a pool better restore state
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void patchForId(DataStoragePatcher consumer, String id) {

        try (Connection connection = connectionSupplier.get()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM FACTORY_HISTORY WHERE ID=?")) {
                statement.setString(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        JsonNode data = JdbcUtil.readTreeFromBlob(resultSet, "factory", objectMapper);
                        JsonNode metadata = JdbcUtil.readTreeFromBlob(resultSet, "factoryMetadata", objectMapper);

                        consumer.patch((ObjectNode) data, metadata, objectMapper);
                        String metadataId = metadata.get("id").asText();

                        List<Blob> allocatedBlobs = new ArrayList<>();
                        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE FACTORY_HISTORY SET factory=?,factoryMetadata=? WHERE id=?")) {
                            JdbcUtil.writeToBlob(updateStatement, 1, out -> objectMapper.writeValue(out, data, OutputStyle.COMPACT), allocatedBlobs);
                            JdbcUtil.writeToBlob(updateStatement, 2, out -> objectMapper.writeValue(out, metadata, OutputStyle.COMPACT), allocatedBlobs);
                            updateStatement.setString(3, metadataId);
                            updateStatement.executeUpdate();
                        } catch (SQLException e1) {
                            throw new RuntimeException(e1);
                        } finally {
                            JdbcUtil.freeBlobs(allocatedBlobs);
                        }
                    }
                }
                connection.commit();
            } finally {
                connection.setAutoCommit(initialAutoCommit);  //connection might be from a pool better restore state
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
