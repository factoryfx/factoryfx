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
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import com.google.common.io.CharStreams;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.MigrationManager;

public class PostgresDataStorage<R extends Data, S> implements DataStorage<R, S> {

    private final R initialFactory;
    private final DataSource dataSource;
    private final MigrationManager<R,S> migrationManager;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public PostgresDataStorage(DataSource dataSource, R defaultFactory, MigrationManager<R,S> migrationManager, ChangeSummaryCreator<R,S> changeSummaryCreator){
        this.dataSource     = dataSource;
        this.initialFactory = defaultFactory;
        this.migrationManager = migrationManager;
        this.changeSummaryCreator=changeSummaryCreator;
    }

    public PostgresDataStorage(DataSource dataSource, R defaultFactory, MigrationManager<R,S> migrationManager){
        this(dataSource, defaultFactory, migrationManager, (d)->null);
    }

    @Override
    public R getHistoryFactory(String id) {
        StoredDataMetadata<S> metaData=null;
        for(StoredDataMetadata<S> historyMetaData: getHistoryFactoryList()){
            if (metaData.id.equals(id)){
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
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new IllegalArgumentException("No factory with id '"+id+"' found");
                        return migrationManager.read(rs.getString(1),metaData);
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
    public DataAndStoredMetadata<R,S> getCurrentFactory() {
        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration");
                 ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next())
                        throw new RuntimeException("No current factory found");

                    StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(rs.getString(2));

                    return new DataAndStoredMetadata<>(
                            migrationManager.read(rs.getString(1), storedDataMetadata), storedDataMetadata
                    );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }


    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiffInfo) {
        final StoredDataMetadata<S> storedDataMetadata = migrationManager.createStoredDataMetadata(
                user,
                comment,
                update.metadata.baseVersionId,
                changeSummaryCreator.createChangeSummary(mergeDiffInfo));
        final DataAndStoredMetadata<R,S> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);

        try {
            try (Connection connection = dataSource.getConnection()) {
                updateCurrentFactory(connection, updateData);
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


    @Override
    public DataAndNewMetadata<R> prepareNewFactory(String currentFactoryStorageId, R currentFactoryCopy){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        migrationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
    }

    @Override
    public String getCurrentFactoryStorageId() {
        return getCurrentFactory().metadata.id;//TODO improve performance
    }

    @Override
    public void loadInitialFactory() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (ResultSet checkTablesExist = connection.getMetaData().getTables(connection.getCatalog(),connection.getSchema(),"currentconfiguration",null)) {
                    if (!checkTablesExist.next()) {
                        createTables(connection);
                        connection.commit();
                    }
                    if (initialFactory != null) {
                        try (PreparedStatement pstmt = connection.prepareStatement("select 1 from currentconfiguration")) {
                            ResultSet rs = pstmt.executeQuery();
                            if (!rs.next()) {
                                StoredDataMetadata<S> metadata = migrationManager.createStoredDataMetadata("System","initial","",null);
                                DataAndStoredMetadata<R,S> initialFactoryAndStorageMetadata = new DataAndStoredMetadata<>(
                                        initialFactory, metadata);
                                updateCurrentFactory(connection,initialFactoryAndStorageMetadata);
                            }
                        }
                        connection.commit();
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Cannot create initial factory",e);
        }
    }

    void createTables(Connection connection) throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("createConfigurationtables.sql"), StandardCharsets.UTF_8)));
        }
    }

    @Override
    public Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        try {
            try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from futureconfigurationmetadata")) {
                    ArrayList<ScheduledDataMetadata<S>> ret = new ArrayList<>();
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
        ScheduledDataMetadata<S> metaData=null;
        for(ScheduledDataMetadata<S> historyMetaData: getFutureFactoryList()){
            if (metaData.id.equals(id)){
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
                    return migrationManager.read(rs.getString(1),metaData);
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factory",e);
        }
    }

    @Override
    public ScheduledDataMetadata<S> addFutureFactory(R futureFactory, NewScheduledDataMetadata futureFactoryMetadata, String user, String comment, MergeDiffInfo<R> mergeDiffInfo) {
        final ScheduledDataMetadata<S> storedFactoryMetadata = migrationManager.createScheduledDataMetadata(user,comment,futureFactoryMetadata.newDataMetadata.baseVersionId,this.changeSummaryCreator.createFutureChangeSummary(mergeDiffInfo),futureFactoryMetadata.scheduled);
        final DataAndScheduledMetadata<R,S> updateData = new DataAndScheduledMetadata<>(futureFactory, storedFactoryMetadata);

        try (Connection connection = this.dataSource.getConnection()) {
            long createdAt = System.currentTimeMillis();
            Timestamp createdAtTimestamp = new Timestamp(createdAt);
            try (PreparedStatement pstmtInsertConfigurationMetadata = connection.prepareStatement("insert into futureconfigurationmetadata (metadata, createdAt, id) values (cast (? as json), ?, ?)")) {
                pstmtInsertConfigurationMetadata.setString(1, migrationManager.writeScheduledMetadata(updateData.metadata));
                pstmtInsertConfigurationMetadata.setTimestamp(2, createdAtTimestamp);
                pstmtInsertConfigurationMetadata.setString(3,storedFactoryMetadata.id);
                pstmtInsertConfigurationMetadata.execute();
            }

            try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement("insert into futureconfiguration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
                pstmtInsertConfiguration.setString(1, migrationManager.write(futureFactory));
                pstmtInsertConfiguration.setString(2, migrationManager.writeScheduledMetadata(updateData.metadata));
                pstmtInsertConfiguration.setTimestamp(3, createdAtTimestamp);
                pstmtInsertConfiguration.setString(4, storedFactoryMetadata.id);
                pstmtInsertConfiguration.execute();
            }

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Cannot add future factory",e);
        }
        return storedFactoryMetadata;
    }



}
