package de.factoryfx.data.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.MigrationManager;

public class FileSystemDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory;

    private final DataAndStoredMetadata<R,S> initialFactory;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R,S> migrationManager;

    public FileSystemDataStorage(Path basePath, DataAndStoredMetadata<R,S> initialFactory, MigrationManager<R,S> migrationManager, FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory){
        this.initialFactory=initialFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.migrationManager = migrationManager;
    }

    public FileSystemDataStorage(Path basePath, DataAndStoredMetadata<R,S> initialFactory, MigrationManager<R,S> migrationManager){
        this(basePath,initialFactory, migrationManager,new FileSystemFactoryStorageHistory<>(basePath, migrationManager));
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
    public DataAndId<R> getCurrentFactory() {
        loadInitialFactory();
        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndId<>(migrationManager.read(readFile(currentFactoryPath), storedDataMetadata), storedDataMetadata.id);
    }


    @Override
    public void updateCurrentFactory(DataAndStoredMetadata<R,S> update) {
        writeFile(currentFactoryPath, migrationManager.write(update.root));
        writeFile(currentFactoryPathMetadata, migrationManager.writeStorageMetadata(update.metadata));
        fileSystemFactoryStorageHistory.updateHistory(update.root, update.metadata);
    }

    private void loadInitialFactory() {
        if (!Files.exists(currentFactoryPath)){
            updateCurrentFactory(new DataAndStoredMetadata<>(initialFactory.root,initialFactory.metadata));
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
