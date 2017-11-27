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

public class PostgresDataStorage<R extends Data> implements DataStorage<R> {

    private final R initialFactory;
    private final DataSource dataSource;
    private final DataSerialisationManager<R> dataSerialisationManager;

    public PostgresDataStorage(DataSource dataSource, R defaultFactory, DataSerialisationManager<R> dataSerialisationManager){
        this.dataSource     = dataSource;
        this.initialFactory = defaultFactory;
        this.dataSerialisationManager = dataSerialisationManager;
    }

    @Override
    public R getHistoryFactory(String id) {
        int dataModelVersion=-99999;
        for(StoredDataMetadata metaData: getHistoryFactoryList()){
            if (metaData.id.equals(id)){
                dataModelVersion=metaData.dataModelVersion;

            }
        }
        if (dataModelVersion==-99999) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from configuration where id = ?")) {
                    pstmt.setString(1,id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new IllegalArgumentException("No factory with id '"+id+"' found");
                        return dataSerialisationManager.read(rs.getString(1),dataModelVersion);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from configuration")) {
                    ArrayList<StoredDataMetadata> ret = new ArrayList<>();
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                           ret.add(dataSerialisationManager.readStoredFactoryMetadata(rs.getString(1)));
                        }
                    }
                    return ret;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public DataAndStoredMetadata<R> getCurrentFactory() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration")) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new RuntimeException("No current factory found");

                        StoredDataMetadata storedDataMetadata = dataSerialisationManager.readStoredFactoryMetadata(rs.getString(2));

                        return new DataAndStoredMetadata<>(
                                dataSerialisationManager.read(rs.getString(1), storedDataMetadata.dataModelVersion), storedDataMetadata
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment) {
        final StoredDataMetadata storedDataMetadata = new StoredDataMetadata();
        storedDataMetadata.creationTime= LocalDateTime.now();
        storedDataMetadata.id= createNewId();
        storedDataMetadata.user=user;
        storedDataMetadata.comment=comment;
        storedDataMetadata.baseVersionId=update.metadata.baseVersionId;
        storedDataMetadata.dataModelVersion=update.metadata.dataModelVersion;
        final DataAndStoredMetadata<R> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);

        try {
            try (Connection connection = dataSource.getConnection()) {
                updateCurrentFactory(connection, updateData);
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update current factory",e);
        }
    }

    private void updateCurrentFactory(Connection connection, DataAndStoredMetadata<R> update) throws SQLException {
        try (PreparedStatement pstmtlockConfiguration = connection.prepareStatement("lock table currentconfiguration in exclusive mode")) {
            pstmtlockConfiguration.execute();
        }
        long createdAt = System.currentTimeMillis();
        boolean firstEntry = false;
        try (PreparedStatement selectMaxCreatedAt = connection.prepareStatement("select max(createdAt) as ts from currentconfiguration")) {
            try (ResultSet maxCreatedAtRs = selectMaxCreatedAt.executeQuery()) {
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
        }
        Timestamp createdAtTimestamp = new Timestamp(createdAt);
        try (PreparedStatement pstmtInsertConfigurationMetadata = connection.prepareStatement("insert into configurationmetadata (metadata, createdAt, id) values (cast (? as json), ?, ?)")) {
            pstmtInsertConfigurationMetadata.setString(1, dataSerialisationManager.writeStorageMetadata(update.metadata));
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

    private void setValues(DataAndStoredMetadata<R> update, Timestamp createdAtTimestamp, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, dataSerialisationManager.write(update.root));
        pstmt.setString(2, dataSerialisationManager.writeStorageMetadata(update.metadata));
        pstmt.setTimestamp(3, createdAtTimestamp);
        pstmt.setString(4, update.metadata.id);
        pstmt.execute();
    }


    @Override
    public DataAndNewMetadata<R> getPrepareNewFactory(){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        dataSerialisationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
    }

    private String createNewId() {
        return UUID.randomUUID().toString();
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
                                StoredDataMetadata metadata = new StoredDataMetadata();
                                String newId = createNewId();
                                metadata.id=newId;
                                metadata.baseVersionId= newId;
                                metadata.user="System";
                                DataAndStoredMetadata<R> initialFactoryAndStorageMetadata = new DataAndStoredMetadata<>(
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
    public Collection<ScheduledDataMetadata> getFutureFactoryList() {
        try {
            try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from futureconfigurationmetadata")) {
                    ArrayList<ScheduledDataMetadata> ret = new ArrayList<>();
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            ret.add(dataSerialisationManager.readScheduledFactoryMetadata(rs.getString(1)));
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
        int dataModelVersion=-99999;
        for(ScheduledDataMetadata metaData: getFutureFactoryList()){
            if (metaData.id.equals(id)){
                dataModelVersion=metaData.dataModelVersion;
            }
        }
        if (dataModelVersion==-99999) {
            throw new IllegalStateException("cant find id: "+id+" in history");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from futureconfiguration where id = ?")) {
                pstmt.setString(1,id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next())
                        throw new IllegalArgumentException("No factory with id '"+id+"' found");
                    return dataSerialisationManager.read(rs.getString(1),dataModelVersion);
                }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read future factory",e);
        }
    }

    @Override
    public void addFutureFactory(DataAndNewMetadata<R> update, String user, String comment, LocalDateTime scheduled) {
        final ScheduledDataMetadata storedFactoryMetadata = new ScheduledDataMetadata();
        storedFactoryMetadata.creationTime= LocalDateTime.now();
        storedFactoryMetadata.id= createNewId();
        storedFactoryMetadata.user=user;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=update.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=update.metadata.dataModelVersion;
        storedFactoryMetadata.scheduled = scheduled;
        update.root.setId(storedFactoryMetadata.id);
        final DataAndScheduledMetadata<R> updateData = new DataAndScheduledMetadata<>(update.root, storedFactoryMetadata);

        try (Connection connection = this.dataSource.getConnection()) {
            long createdAt = System.currentTimeMillis();
            Timestamp createdAtTimestamp = new Timestamp(createdAt);
            try (PreparedStatement pstmtInsertConfigurationMetadata = connection.prepareStatement("insert into futureconfigurationmetadata (metadata, createdAt, id) values (cast (? as json), ?, ?)")) {
                pstmtInsertConfigurationMetadata.setString(1, dataSerialisationManager.writeScheduledMetadata(storedFactoryMetadata));
                pstmtInsertConfigurationMetadata.setTimestamp(2, createdAtTimestamp);
                pstmtInsertConfigurationMetadata.setString(3,storedFactoryMetadata.id);
                pstmtInsertConfigurationMetadata.execute();
            }

            try (PreparedStatement pstmtInsertConfiguration = connection.prepareStatement("insert into futureconfiguration (root, metadata, createdAt, id) values (cast (? as json), cast (? as json), ?, ?)")) {
                pstmtInsertConfiguration.setString(1, dataSerialisationManager.write(update.root));
                pstmtInsertConfiguration.setString(2, dataSerialisationManager.writeScheduledMetadata(updateData.metadata));
                pstmtInsertConfiguration.setTimestamp(3, createdAtTimestamp);
                pstmtInsertConfiguration.setString(4, storedFactoryMetadata.id);
                pstmtInsertConfiguration.execute();
            }

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Cannot add future factory",e);
        }
    }



}
