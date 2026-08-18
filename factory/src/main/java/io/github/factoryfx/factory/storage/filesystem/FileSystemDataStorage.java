package io.github.factoryfx.factory.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.OutputStyle;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.storage.migration.ConfigurationPatch;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;

public class FileSystemDataStorage<R extends FactoryBase<?, R>> implements DataStorage<R> {
    private final FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory;

    private final R initialData;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R> migrationManager;
    private final SimpleObjectMapper objectMapper;
    private String currentIdCache;

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R> migrationManager, FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory, SimpleObjectMapper objectMapper) {
        this.initialData = initialData;

        if (!Files.exists(basePath)) {
            throw new IllegalArgumentException("path don't exists:" + basePath);
        }
        this.currentFactoryPath = basePath.resolve("currentFactory.json");
        this.currentFactoryPathMetadata = basePath.resolve("currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory = fileSystemFactoryStorageHistory;
        this.migrationManager = migrationManager;
        this.objectMapper = objectMapper;

    }

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper) {
        this(basePath, initialData, migrationManager, new FileSystemFactoryStorageHistory<>(basePath, migrationManager, objectMapper), objectMapper);
    }

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R> migrationManager, SimpleObjectMapper objectMapper, int maxConfigurationHistory) {
        this(basePath, initialData, migrationManager, new FileSystemFactoryStorageHistory<>(basePath, migrationManager, objectMapper, maxConfigurationHistory), objectMapper);
    }


    @Override
    public R getHistoryData(String id) {
        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryDataList(boolean light) {
        return fileSystemFactoryStorageHistory.getHistoryFactoryList(light);
    }

    @Override
    public Collection<ScheduledUpdateMetadata> getFutureDataList() {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public void deleteFutureData(String id) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public R getFutureData(String id) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public void addFutureData(ScheduledUpdate<R> futureFactory) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public DataAndId<R> getCurrentData() {
        loadInitialFactory();
        StoredDataMetadata storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata), false);
        currentIdCache = storedDataMetadata.id;
        return new DataAndId<>(migrationManager.read(readFile(currentFactoryPath), storedDataMetadata), storedDataMetadata.id);
    }

    @Override
    public RawFactoryDataAndMetadata getCurrentDataRaw() {
        loadInitialFactory();
        RawFactoryDataAndMetadata raw = new RawFactoryDataAndMetadata();
        raw.root = objectMapper.readTree(readFile(currentFactoryPath));
        raw.metadata = objectMapper.readValue(readFile(currentFactoryPathMetadata), StoredDataMetadata.class);
        return raw;
    }

    @Override
    public String getCurrentDataId() {
        if (currentIdCache == null) {
            loadInitialFactory();
            if (currentIdCache == null) {
                currentIdCache = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata), true).id;
            }
        }
        return currentIdCache;
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, UpdateSummary changeSummary) {
        StoredDataMetadata metadata = new StoredDataMetadata(
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot(), getCurrentDataId(),
                migrationManager.getCurrentConfigurationSchemaVersion());
        update(update.root, metadata);
    }

    @Override
    public void patchAll(ConfigurationPatch consumer) {
        JsonNode data = objectMapper.readTree(currentFactoryPath);
        StoredDataMetadata metadata = objectMapper.readValue(readFile(currentFactoryPathMetadata), StoredDataMetadata.class);
        consumer.patch(new DataJsonNode((ObjectNode) data), metadata, objectMapper);
        writeFile(currentFactoryPath, objectMapper.writeValueAsString(data, OutputStyle.COMPACT));
        writeFile(currentFactoryPathMetadata, objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT));

        fileSystemFactoryStorageHistory.patchAll(consumer);
    }

    @Override
    public void updateCurrentDataRaw(RawFactoryDataAndMetadata rawDataAndMetadata) {
        update(objectMapper.writeValueAsString(rawDataAndMetadata.root, OutputStyle.COMPACT), rawDataAndMetadata.metadata);
    }

    private void update(R update, StoredDataMetadata metadata) {
        update(objectMapper.writeValueAsString(update, OutputStyle.COMPACT), metadata);
    }

    private void update(String rootJson, StoredDataMetadata metadata) {
        String metadataJson = objectMapper.writeValueAsString(metadata, OutputStyle.COMPACT);
        writeFile(currentFactoryPath, rootJson);
        writeFile(currentFactoryPathMetadata, metadataJson);
        fileSystemFactoryStorageHistory.updateHistory(rootJson, metadataJson, metadata);
        currentIdCache = metadata.id;
    }

    private void loadInitialFactory() {
        if (!Files.exists(currentFactoryPath)) {
            StoredDataMetadata metadata = new StoredDataMetadata(LocalDateTime.now(),
                    UUID.randomUUID().toString(),
                    "System",
                    "initial factory",
                    UUID.randomUUID().toString(),
                    null,
                    initialData.internal().createDataStorageMetadataDictionaryFromRoot(),
                    null,
                    migrationManager.getCurrentConfigurationSchemaVersion()
            );
            update(initialData, metadata);
        }
    }

    private String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
