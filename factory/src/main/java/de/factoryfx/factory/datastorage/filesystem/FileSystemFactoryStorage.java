package de.factoryfx.factory.datastorage.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class FileSystemFactoryStorage<L,V,T extends FactoryBase<L,V>> implements FactoryStorage<L,V,T> {
    private final FileSystemFactoryStorageHistory<L,V,T> fileSystemFactoryStorageHistory;

    private T initialFactory;
    private Path currentFactoryPath;
    private Path currentFactoryPathMetadata;
    private final SimpleObjectMapper objectMapper=ObjectMapperBuilder.build();
    private final Class<T> rootClass;

    public FileSystemFactoryStorage(Path basePath, T defaultFactory, Class<T> rootClass, FileSystemFactoryStorageHistory<L,V,T> fileSystemFactoryStorageHistory){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
        this.rootClass=rootClass;
    }

    public FileSystemFactoryStorage(Path basePath, T defaultFactory, Class<T> rootClass){
        this(basePath,defaultFactory,rootClass,new FileSystemFactoryStorageHistory<>(basePath,rootClass));
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
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        return new FactoryAndStorageMetadata<>(objectMapper.readValue(currentFactoryPath.toFile(),rootClass),objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class));
    }

    @Override
    public void updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        objectMapper.writeValue(currentFactoryPath.toFile(),update.root);
        objectMapper.writeValue(currentFactoryPathMetadata.toFile(),update.metadata);
        fileSystemFactoryStorageHistory.updateHistory(update.metadata,update.root);
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
        fileSystemFactoryStorageHistory.initFromFileSystem();
        if (Files.exists(currentFactoryPath)){
            objectMapper.readValue(currentFactoryPath.toFile(),rootClass);
            objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class);
        } else {
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            String newId = UUID.randomUUID().toString();
            metadata.id=newId;
            metadata.baseVersionId= newId;
            metadata.user="System";
            FactoryAndStorageMetadata<T> initialFactoryAndStorageMetadata = new FactoryAndStorageMetadata<T>(
                    initialFactory,metadata);
            updateCurrentFactory(initialFactoryAndStorageMetadata);
        }
    }
}
