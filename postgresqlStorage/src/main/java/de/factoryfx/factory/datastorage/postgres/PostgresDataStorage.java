package de.factoryfx.factory.datastorage.postgres;

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

import com.google.common.io.CharStreams;
import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.MigrationManager;

public class PostgresDataStorage<R extends Data, S> implements DataStorage<R, S> {

    private final R initialData;
    private final DataSource dataSource;
    private final MigrationManager<R,S> migrationManager;
    private final GeneralStorageMetadata generalStorageMetadata;

    public PostgresDataStorage(DataSource dataSource, R initialDataParam, GeneralStorageMetadata generalStorageMetadata, MigrationManager<R,S> migrationManager){
        this.dataSource = dataSource;
        this.initialData = initialDataParam;
        this.generalStorageMetadata = generalStorageMetadata;
        this.migrationManager = migrationManager;
    }

    @Override
    public R getHistoryFactory(String id) {
        StoredDataMetadata<S> metaData=null;
        for(StoredDataMetadata<S> historyMetaData: getHistoryFactoryList()){
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
                        return migrationManager.read(rs.getString(1),metaData.generalStorageMetadata,metaData.dataStorageMetadataDictionary);
                    }
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
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
    public DataAndId<R> getCurrentFactory() {
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
                                null, generalStorageMetadata,
                                initialData.internal().createDataStorageMetadataDictionaryFromRoot()
                        );

                        updateCurrentFactory(connection, new DataAndStoredMetadata<>(initialData,metadata));
                        connection.commit();
                        return new DataAndId<>(initialData, metadata.id);
                    } else {
                        StoredDataMetadata<S> metaData = migrationManager.readStoredFactoryMetadata(rs.getString(2));
                        return new DataAndId<>(migrationManager.read(rs.getString(1), metaData.generalStorageMetadata,metaData.dataStorageMetadataDictionary), metaData.id);
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }


    @Override
    public void updateCurrentFactory(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata =update.createUpdateStoredDataMetadata(changeSummary,generalStorageMetadata);
        update(update.root, metadata);
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
    public Collection<ScheduledUpdateMetadata> getFutureFactoryList() {
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
    public void deleteFutureFactory(String id) {
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

    public R getFutureFactory(String id) {
        ScheduledUpdateMetadata metaData=null;
        for(ScheduledUpdateMetadata historyMetaData: getFutureFactoryList()){
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
                    return migrationManager.read(rs.getString(1),metaData.generalStorageMetadata,metaData.dataStorageMetadataDictionary);
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factory",e);
        }
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
