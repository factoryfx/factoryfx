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
import com.google.common.io.CharStreams;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.*;

public class PostgresDataStorage<R extends FactoryBase<?,R>, S> implements DataStorage<R, S> {

    private final R initialData;
    private final DataSource dataSource;
    private final MigrationManager<R,S> migrationManager;
    private final SimpleObjectMapper objectMapper;

    public PostgresDataStorage(DataSource dataSource, R initialDataParam, MigrationManager<R,S> migrationManager, SimpleObjectMapper objectMapper){
        this.dataSource = dataSource;
        this.initialData = initialDataParam;
        this.migrationManager = migrationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public R getHistoryData(String id) {
        StoredDataMetadata<S> metaData=null;
        for(StoredDataMetadata<S> historyMetaData: getHistoryDataList()){
            if (historyMetaData.id.equals(id)){
                metaData=historyMetaData;

            }
        }
        if (metaData==null) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }

        try {
            try (Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from configuration where id = ?")) {
                    pstmt.setString(1,id);
                    ensureTablesAreAvailable(connection);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new IllegalArgumentException("No factory with id '"+id+"' found");
                        return migrationManager.read(rs.getString(1),metaData.dataStorageMetadataDictionary);
                    }
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryDataList() {
        try {
            try (Connection connection = dataSource.getConnection();
                PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from configuration")) {
                ArrayList<StoredDataMetadata<S>> ret = new ArrayList<>();
                try (ResultSet rs = pstmt.executeQuery()) {
                    ensureTablesAreAvailable(connection);
                    while (rs.next()) {
                       ret.add(migrationManager.readStoredFactoryMetadata(rs.getString(1)));
                    }
                }
                 return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }


    @Override
    public DataAndId<R> getCurrentData() {
        try {
            try (Connection connection = dataSource.getConnection()){
                ensureTablesAreAvailable(connection);
                try (
                    PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration");
                    ResultSet rs = pstmt.executeQuery()) {

                    if (!rs.next()) {//"No current factory found
                        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(LocalDateTime.now(),
                                UUID.randomUUID().toString(),
                                "System",
                                "initial factory",
                                UUID.randomUUID().toString(),
                                null,
                                initialData.internal().createDataStorageMetadataDictionaryFromRoot()
                        );

                        updateCurrentFactory(connection, new DataAndStoredMetadata<>(initialData,metadata));
                        connection.commit();
                        return new DataAndId<>(initialData, metadata.id);
                    } else {
                        StoredDataMetadata<S> metaData = migrationManager.readStoredFactoryMetadata(rs.getString(2));
                        return new DataAndId<>(migrationManager.read(rs.getString(1),metaData.dataStorageMetadataDictionary), metaData.id);
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }


    @Override
    public void updateCurrentData(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata =update.createUpdateStoredDataMetadata(changeSummary);
        update(update.root, metadata);
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
//        patchCurrentData(consumer);
        throw new UnsupportedOperationException();
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        String dataString=null;
        String metadataString=null;
        getCurrentData();//ensure initial data populated
        try {
            try (Connection connection = dataSource.getConnection()){

                try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration");
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        dataString=rs.getString(1);
                        metadataString=rs.getString(2);
                    }
                }

                JsonNode data = objectMapper.readTree(dataString);
                JsonNode metadata = objectMapper.readTree(metadataString);
                consumer.patch(data,metadata);

                try (PreparedStatement pstmt =
                             connection.prepareStatement("update currentconfiguration set root = cast (? as json), metadata = cast (? as json)")) {
                    pstmt.setString(1, objectMapper.writeTree(data));
                    pstmt.setString(2, objectMapper.writeTree(metadata));
                    pstmt.execute();
                }
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    private void update(R update, StoredDataMetadata<S> metadata) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                ensureTablesAreAvailable(connection);
                updateCurrentFactory(connection, new DataAndStoredMetadata<>(update,metadata));
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update current factory",e);
        }
    }

    private void updateCurrentFactory(Connection connection, DataAndStoredMetadata<R,S> update) throws SQLException {
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
                    createdAt = Math.max(createdAt, timestamp.getTime()+1);
                } else {
                    firstEntry = true;
                }
        }
        Timestamp createdAtTimestamp = new Timestamp(createdAt);
        try (PreparedStatement pstmtInsertConfigurationMetadata = connection.prepareStatement("insert into configurationmetadata (metadata, createdAt, id) values (cast (? as json), ?, ?)")) {
            pstmtInsertConfigurationMetadata.setString(1, migrationManager.writeStorageMetadata(update.metadata));
            pstmtInsertConfigurationMetadata.setTimestamp(2, createdAtTimestamp);
            pstmtInsertConfigurationMetadata.setString(3,update.metadata.id);
            pstmtInsertConfigurationMetadata.execute();
        }

        try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement("insert into configuration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
            setValues(update,createdAtTimestamp,pstmtInsertConfiguration);
        }
        try (PreparedStatement pstmtUpdateCurrentConfiguraion =
                     firstEntry?connection.prepareStatement("insert into currentconfiguration (root,metadata,createdAt,id) values (cast (? as json), cast (? as json), ?, ?)")
                             :connection.prepareStatement("update currentconfiguration set root = cast (? as json), metadata = cast (? as json), createdAt = ?, id = ?")) {
            setValues(update,createdAtTimestamp,pstmtUpdateCurrentConfiguraion);
        }
    }

    private void setValues(DataAndStoredMetadata<R,S> update, Timestamp createdAtTimestamp, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, migrationManager.write(update.root));
        pstmt.setString(2, migrationManager.writeStorageMetadata(update.metadata));
        pstmt.setTimestamp(3, createdAtTimestamp);
        pstmt.setString(4, update.metadata.id);
        pstmt.execute();
    }

    private boolean tablesAreAvailable;
    private void ensureTablesAreAvailable(Connection connection) {
        if (!tablesAreAvailable){
            try (ResultSet checkTablesExist = connection.getMetaData().getTables(connection.getCatalog(), connection.getSchema(), "currentconfiguration", null)) {
                if (!checkTablesExist.next()) {
                    createTables(connection);
                    connection.commit();
                    tablesAreAvailable=true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

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
        try {
            try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from futureconfigurationmetadata")) {
                    ArrayList<ScheduledUpdateMetadata> ret = new ArrayList<>();
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            ret.add(migrationManager.readScheduledFactoryMetadata(rs.getString(1)));
                        }
                    }
                    return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factories",e);
        }
    }

    @Override
    public void deleteFutureData(String id) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement pstmtDeleteMetadata =
                         connection.prepareStatement("delete from futureconfigurationmetadata where id = ?");
              PreparedStatement pstmtDeleteConfiguration = connection.prepareStatement("delete from futureconfiguration where id = ?")
            ) {
            pstmtDeleteMetadata.setString(1,id);
            pstmtDeleteMetadata.executeUpdate();
            pstmtDeleteConfiguration.setString(1,id);
            pstmtDeleteConfiguration.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot delete future factory",e);
        }
    }

    public R getFutureData(String id) {
        ScheduledUpdateMetadata metaData=null;
        for(ScheduledUpdateMetadata historyMetaData: getFutureDataList()){
            if (historyMetaData.id.equals(id)){
                metaData=historyMetaData;
            }
        }
        if (metaData==null) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from futureconfiguration where id = ?")) {
                pstmt.setString(1,id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next())
                        throw new IllegalArgumentException("No factory with id '"+id+"' found");
                    return migrationManager.read(rs.getString(1), metaData.dataStorageMetadataDictionary);
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factory",e);
        }
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


        try (Connection connection = this.dataSource.getConnection()) {
            ensureTablesAreAvailable(connection);
            long createdAt = System.currentTimeMillis();
            Timestamp createdAtTimestamp = new Timestamp(createdAt);
            try (PreparedStatement pstmtInsertConfigurationMetadata = connection.prepareStatement("insert into futureconfigurationmetadata (metadata, createdAt, id) values (cast (? as json), ?, ?)")) {
                pstmtInsertConfigurationMetadata.setString(1, migrationManager.writeScheduledUpdateMetadata(scheduledUpdateMetadata));
                pstmtInsertConfigurationMetadata.setTimestamp(2, createdAtTimestamp);
                pstmtInsertConfigurationMetadata.setString(3,scheduledUpdateMetadata.id);
                pstmtInsertConfigurationMetadata.execute();
            }

            try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement("insert into futureconfiguration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
                pstmtInsertConfiguration.setString(1, migrationManager.write(futureFactory.root));
                pstmtInsertConfiguration.setString(2, migrationManager.writeScheduledUpdateMetadata(scheduledUpdateMetadata));
                pstmtInsertConfiguration.setTimestamp(3, createdAtTimestamp);
                pstmtInsertConfiguration.setString(4, scheduledUpdateMetadata.id);
                pstmtInsertConfiguration.execute();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot add future factory",e);
        }
    }
}
