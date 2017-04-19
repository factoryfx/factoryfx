package de.factoryfx.factory.datastorage.filesystem;

import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactoryAndStoredMetadata;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.NewFactoryMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class FileSystemFactoryStorage<L,V,T extends FactoryBase<L,V>> implements FactoryStorage<L,V,T> {
    private final FileSystemFactoryStorageHistory<L,V,T> fileSystemFactoryStorageHistory;

    private T initialFactory;
    private Path currentFactoryPath;
    private Path currentFactoryPathMetadata;
    private final FactorySerialisationManager<T> factorySerialisationManager;

    public FileSystemFactoryStorage(Path basePath, T defaultFactory, FactorySerialisationManager<T> factorySerialisationManager, FileSystemFactoryStorageHistory<L,V,T> fileSystemFactoryStorageHistory){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.factorySerialisationManager=factorySerialisationManager;
    }

    public FileSystemFactoryStorage(Path basePath, T defaultFactory, FactorySerialisationManager<T> factorySerialisationManager){
        this(basePath,defaultFactory,factorySerialisationManager,new FileSystemFactoryStorageHistory<>(basePath,factorySerialisationManager));
    }


    @Override
    public T getHistoryFactory(String id) {
        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return fileSystemFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public FactoryAndStoredMetadata<T> getCurrentFactory() {
        StoredFactoryMetadata storedFactoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
        return new FactoryAndStoredMetadata<>(factorySerialisationManager.read(readFile(currentFactoryPath),storedFactoryMetadata.dataModelVersion),storedFactoryMetadata);
    }

    @Override
    public void updateCurrentFactory(FactoryAndNewMetadata<T> update, String user, String comment) {
        final StoredFactoryMetadata storedFactoryMetadata = new StoredFactoryMetadata();
        storedFactoryMetadata.creationTime= LocalDateTime.now();
        storedFactoryMetadata.id= UUID.randomUUID().toString();
        storedFactoryMetadata.user=comment;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=update.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=update.metadata.dataModelVersion;
        final FactoryAndStoredMetadata<T> updateData = new FactoryAndStoredMetadata<>(update.root, storedFactoryMetadata);

        writeFile(currentFactoryPath,factorySerialisationManager.write(updateData.root));
        writeFile(currentFactoryPathMetadata,factorySerialisationManager.writeStorageMetadata(updateData.metadata));
        fileSystemFactoryStorageHistory.updateHistory(updateData.metadata,updateData.root);
    }

    @Override
    public FactoryAndNewMetadata<T> getPrepareNewFactory(){
        NewFactoryMetadata metadata = new NewFactoryMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
        factorySerialisationManager.prepareNewFactoryMetadata(metadata);
        return new FactoryAndNewMetadata<>(getCurrentFactory().root,metadata);
    }


    @Override
    public void loadInitialFactory() {
        fileSystemFactoryStorageHistory.initFromFileSystem();
        if (Files.exists(currentFactoryPath)){
//            objectMapper.readValue(currentFactoryPath.toFile(),rootClass);
//            objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class);
        } else {
            NewFactoryMetadata metadata = new NewFactoryMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            FactoryAndNewMetadata<T> initialFactoryAndStorageMetadata = new FactoryAndNewMetadata<>(initialFactory,metadata);
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
