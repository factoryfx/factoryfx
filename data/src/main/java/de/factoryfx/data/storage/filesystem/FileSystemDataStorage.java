package de.factoryfx.data.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.MigrationManager;

public class FileSystemDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory;

    private final R initialData;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R,S> migrationManager;
    private final GeneralStorageMetadata generalStorageMetadata;

    public FileSystemDataStorage(Path basePath, R initialData, GeneralStorageMetadata generalStorageMetadata, MigrationManager<R,S> migrationManager, FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory){
        this.initialData = initialData;
        this.generalStorageMetadata=generalStorageMetadata;

        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.migrationManager = migrationManager;

    }

    public FileSystemDataStorage(Path basePath, R initialData, GeneralStorageMetadata generalStorageMetadata, MigrationManager<R,S> migrationManager){
        this(basePath, initialData, generalStorageMetadata, migrationManager,new FileSystemFactoryStorageHistory<>(basePath, migrationManager));
    }

    @Override
    public R getHistoryFactory(String id) {
        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return fileSystemFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public Collection<ScheduledUpdateMetadata> getFutureFactoryList() {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public void deleteFutureFactory(String id) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public R getFutureFactory(String id) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public void addFutureFactory(ScheduledUpdate<R> futureFactory) {
        throw new UnsupportedOperationException();//TODO
    }

    @Override
    public DataAndId<R> getCurrentFactory() {
        loadInitialFactory();
        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndId<>(migrationManager.read(readFile(currentFactoryPath), storedDataMetadata.generalStorageMetadata,storedDataMetadata.dataStorageMetadataDictionary), storedDataMetadata.id);
    }

    @Override
    public void updateCurrentFactory(DataUpdate<R> update, S changeSummary) {
        StoredDataMetadata<S> metadata = new StoredDataMetadata<>(
                UUID.randomUUID().toString(),
                update.user,
                update.comment,
                update.baseVersionId,
                changeSummary,
                generalStorageMetadata,
                update.root.internal().createDataStorageMetadataDictionaryFromRoot());
        update(update.root, metadata);
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
                    null, generalStorageMetadata,
                    initialData.internal().createDataStorageMetadataDictionaryFromRoot()
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
