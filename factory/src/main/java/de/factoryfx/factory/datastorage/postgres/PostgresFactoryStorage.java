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
import java.util.UUID;

import javax.sql.DataSource;

import com.google.common.io.CharStreams;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class PostgresFactoryStorage<L,V,T extends FactoryBase<L,V>> implements FactoryStorage<L,V,T> {

    private T initialFactory;
    private final DataSource dataSource;
    private final SimpleObjectMapper objectMapper=ObjectMapperBuilder.build();
    private final Class<T> rootClass;

    public PostgresFactoryStorage(DataSource dataSource, T defaultFactory, Class<T> rootClass){
        this.dataSource     = dataSource;
        this.initialFactory = defaultFactory;
        this.rootClass=rootClass;
    }

    @Override
    public T getHistoryFactory(String id) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root from configuration where id = ?")) {
                    pstmt.setString(1,id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new IllegalArgumentException("No factory with id '"+id+"' found");
                        return objectMapper.readValue(rs.getString(1),rootClass);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (metadata as text) as metadata from configuration")) {
                    ArrayList<StoredFactoryMetadata> ret = new ArrayList<>();
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                           ret.add(objectMapper.readValue(rs.getString(1),StoredFactoryMetadata.class));
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
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement pstmt = connection.prepareStatement("select cast (root as text) as root, cast (metadata as text) as metadata from currentconfiguration")) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next())
                            throw new RuntimeException("No current factory found");
                        return new FactoryAndStorageMetadata<>(
                                objectMapper.readValue(rs.getString(1),rootClass),
                                objectMapper.readValue(rs.getString(2),StoredFactoryMetadata.class)
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read current factory",e);
        }
    }

    @Override
    public void updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                updateCurrentFactory(connection, update);
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update current factory",e);
        }
    }

    private void updateCurrentFactory(Connection connection, FactoryAndStorageMetadata<T> update) throws SQLException {
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
            pstmtInsertConfigurationMetadata.setString(1,objectMapper.writeValueAsString(update.metadata));
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

    private void setValues(FactoryAndStorageMetadata<T> update, Timestamp createdAtTimestamp, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, objectMapper.writeValueAsString(update.root));
        pstmt.setString(2, objectMapper.writeValueAsString(update.metadata));
        pstmt.setTimestamp(3, createdAtTimestamp);
        pstmt.setString(4, update.metadata.id);
        pstmt.execute();
    }


    @Override
    public FactoryAndStorageMetadata<T> getPrepareNewFactory(){
        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id=UUID.randomUUID().toString();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        return new FactoryAndStorageMetadata<>(getCurrentFactory().root,metadata);
    }


    @Override
    public void loadInitialFactory() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (ResultSet checkTablesExist = connection.getMetaData().getTables(connection.getCatalog(),connection.getSchema(),"currentconfiguration",null)) {
                    if (!checkTablesExist.next()) {
                        createTables(connection);
                        if (initialFactory != null) {
                            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
                            String newId = UUID.randomUUID().toString();
                            metadata.id=newId;
                            metadata.baseVersionId= newId;
                            metadata.user="System";
                            FactoryAndStorageMetadata<T> initialFactoryAndStorageMetadata = new FactoryAndStorageMetadata<T>(
                                    initialFactory,metadata);
                            updateCurrentFactory(connection,initialFactoryAndStorageMetadata);
                        }
                        connection.commit();
                    }
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Cannot create initial factory",e);
        }
    }

    private void createTables(Connection connection) throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("createConfigurationtables.sql"), StandardCharsets.UTF_8)));
        }
    }
}
