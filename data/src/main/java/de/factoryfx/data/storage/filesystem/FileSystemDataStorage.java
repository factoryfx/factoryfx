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
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.NewDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;

public class FileSystemDataStorage<R extends Data> implements DataStorage<R> {
    private final FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory;

    private R initialFactory;
    private Path currentFactoryPath;
    private Path currentFactoryPathMetadata;
    private final DataSerialisationManager<R> dataSerialisationManager;

    public FileSystemDataStorage(Path basePath, R defaultFactory, DataSerialisationManager<R> dataSerialisationManager, FileSystemFactoryStorageHistory<R> fileSystemFactoryStorageHistory){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.dataSerialisationManager = dataSerialisationManager;
    }

    public FileSystemDataStorage(Path basePath, R defaultFactory, DataSerialisationManager<R> dataSerialisationManager){
        this(basePath,defaultFactory, dataSerialisationManager,new FileSystemFactoryStorageHistory<>(basePath, dataSerialisationManager));
    }


    @Override
    public R getHistoryFactory(String id) {
        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return fileSystemFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public DataAndStoredMetadata<R> getCurrentFactory() {
        StoredDataMetadata storedDataMetadata = dataSerialisationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new DataAndStoredMetadata<>(dataSerialisationManager.read(readFile(currentFactoryPath), storedDataMetadata.dataModelVersion), storedDataMetadata);
    }

    @Override
    public void updateCurrentFactory(DataAndNewMetadata<R> update, String user, String comment) {
        final StoredDataMetadata storedDataMetadata = new StoredDataMetadata();
        storedDataMetadata.creationTime= LocalDateTime.now();
        storedDataMetadata.id= UUID.randomUUID().toString();
        storedDataMetadata.user=user;
        storedDataMetadata.comment=comment;
        storedDataMetadata.baseVersionId=update.metadata.baseVersionId;
        storedDataMetadata.dataModelVersion=update.metadata.dataModelVersion;
        final DataAndStoredMetadata<R> updateData = new DataAndStoredMetadata<>(update.root, storedDataMetadata);

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
        if (Files.exists(currentFactoryPath)){
//            objectMapper.readValue(currentFactoryPath.toFile(),rootClass);
//            objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class);
        } else {
            NewDataMetadata metadata = new NewDataMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            DataAndNewMetadata<R> initialFactoryAndStorageMetadata = new DataAndNewMetadata<>(initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata,"System","initial factory");
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
