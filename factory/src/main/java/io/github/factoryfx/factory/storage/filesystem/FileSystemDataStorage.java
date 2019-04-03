package io.github.factoryfx.factory.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.storage.*;
import io.github.factoryfx.factory.storage.migration.MigrationManager;

public class FileSystemDataStorage<R extends FactoryBase<?,R>,S> implements DataStorage<R,S> {
    private final FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory;

    private final R initialData;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R,S> migrationManager;
    private final SimpleObjectMapper objectMapper;

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R,S> migrationManager, FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory, SimpleObjectMapper objectMapper){
        this.initialData = initialData;

        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.migrationManager = migrationManager;
        this.objectMapper = objectMapper;

    }

    public FileSystemDataStorage(Path basePath, R initialData, MigrationManager<R,S> migrationManager, SimpleObjectMapper objectMapper){
         this(basePath, initialData, migrationManager,new FileSystemFactoryStorageHistory<>(basePath, migrationManager),objectMapper);
    }

    @Override
    public R getHistoryData(String id) {
        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryDataList() {
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
        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndId<>(migrationManager.read(readFile(currentFactoryPath),storedDataMetadata.dataStorageMetadataDictionary), storedDataMetadata.id);
    }

    @Override
    public void updateCurrentData(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot(),getCurrentData().id);
        update(update.root, metadata);
    }

    @Override
    public void patchAll(DataStoragePatcher consumer) {
        patchCurrentData(consumer);
        fileSystemFactoryStorageHistory.patchAll(consumer, objectMapper);
    }

    @Override
    public void patchCurrentData(DataStoragePatcher consumer) {
        JsonNode data = objectMapper.readTree(currentFactoryPath);
        JsonNode metadata = objectMapper.readTree(currentFactoryPathMetadata);
        consumer.patch(data,metadata);
        writeFile(currentFactoryPath, objectMapper.writeTree(data));
        writeFile(currentFactoryPathMetadata, objectMapper.writeTree(metadata));
    }

    private void update(R update, StoredDataMetadata<S> metadata) {
        writeFile(currentFactoryPath, migrationManager.write(update));
        writeFile(currentFactoryPathMetadata, migrationManager.writeStorageMetadata(metadata));
        fileSystemFactoryStorageHistory.updateHistory(update, metadata);
    }

    private void loadInitialFactory() {
        if (!Files.exists(currentFactoryPath)){
            StoredDataMetadata<S> metadata = new StoredDataMetadata<>(LocalDateTime.now(),
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

    private String readFile(Path path){
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content){
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
