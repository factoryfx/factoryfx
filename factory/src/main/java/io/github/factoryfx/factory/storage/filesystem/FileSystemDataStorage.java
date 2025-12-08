package io.github.factoryfx.factory.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

public class FileSystemDataStorage<R extends FactoryBase<?, R>> implements DataStorage<R> {
    private final FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory;

    private final R initialData;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R> migrationManager;
    private final SimpleObjectMapper objectMapper;

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R> migrationManager, FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory, SimpleObjectMapper objectMapper) {
        this.initialData = initialData;

        if (!Files.exists(basePath)) {
            throw new IllegalArgumentException("path don't exists:" + basePath);
        }
        this.currentFactoryPath = Paths.get(basePath + "/currentFactory.json");
        this.currentFactoryPathMetadata = Paths.get(basePath + "/currentFactory_metadata.json");
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
    public Collection<StoredDataMetadata> getHistoryDataList() {
        return fileSystemFactoryStorageHistory.getHistoryFactoryList();
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
        StoredDataMetadata storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndId<>(migrationManager.read(readFile(currentFactoryPath), storedDataMetadata.dataStorageMetadataDictionary), storedDataMetadata.id);
    }

    @Override
    public String getCurrentDataId() {
        return getCurrentData().id;//TODO optimize
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, UpdateSummary changeSummary) {
        StoredDataMetadata metadata = new StoredDataMetadata(
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot(), getCurrentDataId());
        update(update.root, metadata);
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
        patchCurrentData(consumer);
        fileSystemFactoryStorageHistory.patchAll(consumer);
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        JsonNode data = objectMapper.readTree(currentFactoryPath);
        JsonNode metadata = objectMapper.readTree(currentFactoryPathMetadata);
        consumer.patch((ObjectNode) data, metadata, objectMapper);
        writeFile(currentFactoryPath, objectMapper.writeValueAsString(data));
        writeFile(currentFactoryPathMetadata, objectMapper.writeValueAsString(metadata));

        fileSystemFactoryStorageHistory.patchForId(consumer, getCurrentDataId());
    }

    private void update(R update, StoredDataMetadata metadata) {
        writeFile(currentFactoryPath, objectMapper.writeValueAsString(update));
        writeFile(currentFactoryPathMetadata, objectMapper.writeValueAsString(metadata));
        fileSystemFactoryStorageHistory.updateHistory(update, metadata);
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
                    null

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
