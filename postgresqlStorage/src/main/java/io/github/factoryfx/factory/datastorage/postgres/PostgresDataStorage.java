package io.github.factoryfx.factory.datastorage.postgres;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.CharStreams;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.OutputStyle;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.DataAndId;
import io.github.factoryfx.factory.storage.DataAndStoredMetadata;
import io.github.factoryfx.factory.storage.DataStorage;
import io.github.factoryfx.factory.storage.DataStoragePatcher;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.ScheduledUpdate;
import io.github.factoryfx.factory.storage.ScheduledUpdateMetadata;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.storage.UpdateSummary;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

public class PostgresDataStorage<R extends FactoryBase<?, R>> implements DataStorage<R> {
    private final R initialData;
    private final DataSource dataSource;
    private final MigrationManager<R> migrationManager;
    private final SimpleObjectMapper objectMapper;

    public PostgresDataStorage(DataSource dataSource, R initialDataParam, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper) {
        this.dataSource = dataSource;
        this.initialData = initialDataParam;
        this.migrationManager = migrationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public R getHistoryData(String id) {
        try (Connection connection = ensureTablesAreAvailable(dataSource.getConnection());
             PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root,  cast (metadata as text) as metadata from configuration where id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {throw new IllegalArgumentException("No factory with id '" + id + "' found");}
                StoredDataMetadata storedDataMetadata = migrationManager.readStoredFactoryMetadata(rs.getString(2));
                return migrationManager.read(rs.getString(1),
                                             storedDataMetadata.dataStorageMetadataDictionary);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory", e);
        }
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryDataList() {
        try (Connection connection = ensureTablesAreAvailable(dataSource.getConnection());
             PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from configuration");
             ResultSet rs = pstmt.executeQuery()) {
            ArrayList<StoredDataMetadata> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(migrationManager.readStoredFactoryMetadata(rs.getString(1)));
            }
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory", e);
        }
    }

    @Override
    public DataAndId<R> getCurrentData() {
        try (Connection connection = ensureTablesAreAvailable(dataSource.getConnection());
             PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration");
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.next()) {//"No current factory found
                StoredDataMetadata metadata = initCurrentData(connection);
                return new DataAndId<>(initialData, metadata.id);
            } else {
                StoredDataMetadata metaData = migrationManager.readStoredFactoryMetadata(rs.getString(2));
                return new DataAndId<>(migrationManager.read(rs.getString(1), metaData.dataStorageMetadataDictionary), metaData.id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory", e);
        }
    }

    @Override
    public String getCurrentDataId() {
        try (Connection connection = ensureTablesAreAvailable(dataSource.getConnection());
             PreparedStatement pstmt = connection.prepareStatement("select id from currentconfiguration");
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.next()) {//"No current factory found
                StoredDataMetadata metadata = initCurrentData(connection);
                return metadata.id;
            } else {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory", e);
        }
    }

    private StoredDataMetadata initCurrentData(Connection connection) throws SQLException {
        StoredDataMetadata metadata = new StoredDataMetadata(LocalDateTime.now(),
                                                             UUID.randomUUID().toString(),
                                                             "System",
                                                             "initial factory",
                                                             UUID.randomUUID().toString(),
                                                             null,
                                                             initialData.internal().createDataStorageMetadataDictionaryFromRoot(),
                                                             null);

        updateCurrentFactory(connection, new DataAndStoredMetadata<>(initialData, metadata));
        connection.commit();
        return metadata;
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, UpdateSummary changeSummary) {
        StoredDataMetadata metadata = update.createUpdateStoredDataMetadata(changeSummary, getCurrentDataId());
        update(update.root, metadata);
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
        String currentId = getCurrentDataId();//ensure initial data populated
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedSelect = connection.prepareStatement("select id, cast (root as text) as root, cast (metadata as text) as metadata from configuration order by createdat");
             ResultSet rs = preparedSelect.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString(1);
                JsonNode data = objectMapper.readTree(rs.getString(2));
                JsonNode metadata = objectMapper.readTree(rs.getString(3));

                consumer.patch((ObjectNode) data, metadata, objectMapper);

                try (PreparedStatement preparedUpdate = connection.prepareStatement("update configuration set root = cast (? as json), metadata = cast (? as json) where id = ?")) {
                    preparedUpdate.setString(1, objectMapper.writeValueAsString(data, OutputStyle.COMPACT));
                    preparedUpdate.setString(2, objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));
                    preparedUpdate.setString(3, id);
                    preparedUpdate.execute();
                }
                if(id.equals(currentId)){
                    try (PreparedStatement pstmt = connection.prepareStatement("update currentconfiguration set root = cast (? as json), metadata = cast (? as json)")) {
                        pstmt.setString(1, objectMapper.writeValueAsString(data));
                        pstmt.setString(2, objectMapper.writeValueAsString(metadata));
                        pstmt.execute();
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot patch configuration", e);
        }
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        String dataString = null;
        String metadataString = null;
        String currentId = getCurrentDataId();//ensure initial data populated

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration");
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dataString = rs.getString(1);
                    metadataString = rs.getString(2);
                }
            }

            JsonNode data = objectMapper.readTree(dataString);
            JsonNode metadata = objectMapper.readTree(metadataString);
            consumer.patch((ObjectNode) data, metadata, objectMapper);

            try (PreparedStatement pstmt =
                     connection.prepareStatement("update currentconfiguration set root = cast (? as json), metadata = cast (? as json)")) {
                pstmt.setString(1, objectMapper.writeValueAsString(data));
                pstmt.setString(2, objectMapper.writeValueAsString(metadata));
                pstmt.execute();
            }

            try (PreparedStatement pstmt =
                     connection.prepareStatement("update configuration set root = cast (? as json), metadata = cast (? as json) where id = ?")) {
                pstmt.setString(1, objectMapper.writeValueAsString(data, OutputStyle.COMPACT));
                pstmt.setString(2, objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));
                pstmt.setString(3, currentId);
                pstmt.execute();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory", e);
        }
    }

    private void update(R update, StoredDataMetadata metadata) {
        try (Connection connection = ensureTablesAreAvailable(dataSource.getConnection())) {
            updateCurrentFactory(connection, new DataAndStoredMetadata<>(update, metadata));
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update current factory", e);
        }
    }

    private void updateCurrentFactory(Connection connection, DataAndStoredMetadata<R> update) throws SQLException {
        try (PreparedStatement pstmtlockConfiguration = connection.prepareStatement("lock table currentconfiguration in exclusive mode")) {
            pstmtlockConfiguration.execute();
        }
        long createdAt = System.currentTimeMillis();
        boolean firstEntry = false;
        try (PreparedStatement selectMaxCreatedAt = connection.prepareStatement("select max(createdAt) as ts from currentconfiguration");
             ResultSet maxCreatedAtRs = selectMaxCreatedAt.executeQuery()) {
            Timestamp timestamp = null;
            if (maxCreatedAtRs.next()) {
                timestamp = maxCreatedAtRs.getTimestamp(1);
            }
            if (timestamp != null) {
                createdAt = Math.max(createdAt, timestamp.getTime() + 1);
            } else {
                firstEntry = true;
            }
        }
        Timestamp createdAtTimestamp = new Timestamp(createdAt);

        try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement("insert into configuration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
            pstmtInsertConfiguration.setString(1, objectMapper.writeValueAsString(update.root, OutputStyle.COMPACT));
            pstmtInsertConfiguration.setString(2, objectMapper.writeValueAsString(update.metadata, OutputStyle.COMPACT));
            pstmtInsertConfiguration.setTimestamp(3, createdAtTimestamp);
            pstmtInsertConfiguration.setString(4, update.metadata.id);
            pstmtInsertConfiguration.execute();
        }
        try (PreparedStatement pstmtUpdateCurrentConfiguraion =
                 firstEntry ? connection.prepareStatement("insert into currentconfiguration (root,metadata,createdAt,id) values (cast (? as json), cast (? as json), ?, ?)")
                     : connection.prepareStatement("update currentconfiguration set root = cast (? as json), metadata = cast (? as json), createdAt = ?, id = ?")) {
            pstmtUpdateCurrentConfiguraion.setString(1, objectMapper.writeValueAsString(update.root));
            pstmtUpdateCurrentConfiguraion.setString(2, objectMapper.writeValueAsString(update.metadata));
            pstmtUpdateCurrentConfiguraion.setTimestamp(3, createdAtTimestamp);
            pstmtUpdateCurrentConfiguraion.setString(4, update.metadata.id);
            pstmtUpdateCurrentConfiguraion.execute();
        }
    }

    private boolean tablesAreAvailable;

    private Connection ensureTablesAreAvailable(Connection connection) {
        if (!tablesAreAvailable) {
            try (ResultSet checkTablesExist = connection.getMetaData().getTables(connection.getCatalog(), connection.getSchema(), "currentconfiguration", null)) {
                if (!checkTablesExist.next()) {
                    createTables(connection);
                    connection.commit();
                }
                tablesAreAvailable = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    void createTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("createConfigurationtables.sql"), StandardCharsets.UTF_8)));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<ScheduledUpdateMetadata> getFutureDataList() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from futureconfiguration");
             ResultSet rs = pstmt.executeQuery()) {
            ArrayList<ScheduledUpdateMetadata> ret = new ArrayList<>();
            while (rs.next()) {
                ret.add(migrationManager.readScheduledFactoryMetadata(rs.getString(1)));
            }
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factories", e);
        }
    }

    @Override
    public void deleteFutureData(String id) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement pstmtDeleteConfiguration = connection.prepareStatement("delete from futureconfiguration where id = ?")) {
            pstmtDeleteConfiguration.setString(1, id);
            pstmtDeleteConfiguration.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot delete future factory", e);
        }
    }

    public R getFutureData(String id) {
        ScheduledUpdateMetadata metaData = null;
        for (ScheduledUpdateMetadata historyMetaData : getFutureDataList()) {
            if (historyMetaData.id.equals(id)) {
                metaData = historyMetaData;
            }
        }
        if (metaData == null) {
            throw new IllegalStateException("cant find id: " + id + " in history");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from futureconfiguration where id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {throw new IllegalArgumentException("No factory with id '" + id + "' found");}
                return migrationManager.read(rs.getString(1), metaData.dataStorageMetadataDictionary);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factory", e);
        }
    }

    @Override
    public void addFutureData(ScheduledUpdate<R> futureFactory) {

        ScheduledUpdateMetadata scheduledUpdateMetadata =
            new ScheduledUpdateMetadata(
                UUID.randomUUID().toString(),
                futureFactory.user,
                futureFactory.comment,
                futureFactory.scheduled,
                futureFactory.root.internal().createDataStorageMetadataDictionaryFromRoot()
            );

        try (Connection connection = this.dataSource.getConnection()) {
            ensureTablesAreAvailable(connection);
            long createdAt = System.currentTimeMillis();
            Timestamp createdAtTimestamp = new Timestamp(createdAt);

            try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement(
                "insert into futureconfiguration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
                pstmtInsertConfiguration.setString(1, objectMapper.writeValueAsString(futureFactory.root));
                pstmtInsertConfiguration.setString(2, objectMapper.writeValueAsString(scheduledUpdateMetadata));
                pstmtInsertConfiguration.setTimestamp(3, createdAtTimestamp);
                pstmtInsertConfiguration.setString(4, scheduledUpdateMetadata.id);
                pstmtInsertConfiguration.execute();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot add future factory", e);
        }
    }
}
