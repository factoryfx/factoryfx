package de.factoryfx.data.storage.filesystem;

import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.*;

public class FileSystemDataStorage<R extends Data,S> implements DataStorage<R,S> {
    private final FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory;

    private final R initialFactory;
    private final Path currentFactoryPath;
    private final Path currentFactoryPathMetadata;
    private final DataSerialisationManager<R,S> dataSerialisationManager;
    private final ChangeSummaryCreator<R,S> changeSummaryCreator;

    public FileSystemDataStorage(Path basePath, R defaultFactory, DataSerialisationManager<R,S> dataSerialisationManager, FileSystemFactoryStorageHistory<R,S> fileSystemFactoryStorageHistory, ChangeSummaryCreator<R,S> changeSummaryCreator){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.dataSerialisationManager = dataSerialisationManager;
        this.changeSummaryCreator=changeSummaryCreator;
    }

    public FileSystemDataStorage(Path basePath, R defaultFactory, DataSerialisationManager<R,S> dataSerialisationManager){
        this(basePath,defaultFactory, dataSerialisationManager,new FileSystemFactoryStorageHistory<>(basePath, dataSerialisationManager),(d)->null);
    }

    public FileSystemDataStorage(Path basePath, R defaultFactory, DataSerialisationManager<R,S> dataSerialisationManager, ChangeSummaryCreator<R,S> changeSummaryCreator){
        this(basePath,defaultFactory, dataSerialisationManager,new FileSystemFactoryStorageHistory<>(basePath, dataSerialisationManager),changeSummaryCreator);
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
        StoredDataMetadata<S> storedDataMetadata = dataSerialisationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndStoredMetadata<>(dataSerialisationManager.read(readFile(currentFactoryPath), storedDataMetadata.dataModelVersion), storedDataMetadata);
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment, MergeDiffInfo<R> mergeDiff) {
        S changeSummary = null;
        if (mergeDiff!=null){
            changeSummary=changeSummaryCreator.createChangeSummary(mergeDiff);
        }
        final StoredDataMetadata<S> storedDataMetadata = new StoredDataMetadata<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                user,
                comment,
                update.metadata.baseVersionId,
                update.metadata.dataModelVersion, changeSummary
        );

        final DataAndStoredMetadata<R,S> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);

        writeFile(currentFactoryPath, dataSerialisationManager.write(updateData.root));
        writeFile(currentFactoryPathMetadata, dataSerialisationManager.writeStorageMetadata(updateData.metadata));
        fileSystemFactoryStorageHistory.updateHistory(updateData.metadata,updateData.root);
    }

    @Override
    public DataAndNewMetadata<R> getPrepareNewFactory(){
        NewDataMetadata metadata = new NewDataMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        dataSerialisationManager.prepareNewFactoryMetadata(metadata);
        return new DataAndNewMetadata<>(getCurrentFactory().root,metadata);
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
            return new String(readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String content){
        try {
            Files.write(path,content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
