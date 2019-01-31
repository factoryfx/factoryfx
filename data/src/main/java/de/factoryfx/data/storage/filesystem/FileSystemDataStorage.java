package de.factoryfx.data.storage.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;
import de.factoryfx.data.storage.migration.MigrationManager;

public class FileSystemDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory;

    private final R initialFactory;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final MigrationManager<R,S> migrationManager;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public FileSystemDataStorage(Path basePath, R defaultFactory, MigrationManager<R,S> migrationManager, FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory, ChangeSummaryCreator<R,S> changeSummaryCreator){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.migrationManager = migrationManager;
        this.changeSummaryCreator=changeSummaryCreator;
    }

    public FileSystemDataStorage(Path basePath, R defaultFactory, MigrationManager<R,S> migrationManager){
        this(basePath,defaultFactory, migrationManager,new FileSystemFactoryStorageHistory<>(basePath, migrationManager),(d)->null);
    }

    public FileSystemDataStorage(Path basePath, R defaultFactory, MigrationManager<R,S> migrationManager, ChangeSummaryCreator<R,S> changeSummaryCreator){
        this(basePath,defaultFactory, migrationManager,new FileSystemFactoryStorageHistory<>(basePath, migrationManager),changeSummaryCreator);
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
    public DataAndStoredMetadata<R,S> getCurrentFactory() {
        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndStoredMetadata<>(migrationManager.read(readFile(currentFactoryPath), storedDataMetadata), storedDataMetadata);
    }

    @Override
    public String getCurrentFactoryStorageId() {
        StoredDataMetadata<S> storedDataMetadata = migrationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return storedDataMetadata.id;
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiff) {
        S changeSummary = null;
        if (mergeDiff!=null){
            changeSummary=changeSummaryCreator.createChangeSummary(mergeDiff);
        }
        StoredDataMetadata<S> storedDataMetadata = migrationManager.createStoredDataMetadata(user, comment, update.metadata.baseVersionId, changeSummary);

        final DataAndStoredMetadata<R,S> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);

        writeFile(currentFactoryPath, migrationManager.write(updateData.root));
        writeFile(currentFactoryPathMetadata, migrationManager.writeStorageMetadata(storedDataMetadata));
        fileSystemFactoryStorageHistory.updateHistory(updateData.metadata,updateData.root);
    }

    @Override
    public DataAndNewMetadata<R> prepareNewFactory(String currentFactoryStorageId, R currentFactoryCopy){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=currentFactoryStorageId;
        migrationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(currentFactoryCopy,metadata);
    }


    @Override
    public void loadInitialFactory() {
        fileSystemFactoryStorageHistory.initFromFileSystem();
        if (!Files.exists(currentFactoryPath)){
            NewDataMetadata metadata = new NewDataMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            DataAndNewMetadata<R> initialFactoryAndStorageMetadata = new DataAndNewMetadata<>(initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata,"System","initial factory",null);
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
