package de.factoryfx.factory.datastorage.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.jackson.SimpleObjectMapper;

public class FileSystemFactoryStorage<T extends FactoryBase<? extends LiveObject<?>, T>> implements FactoryStorage<T> {
    private final FileSystemFactoryStorageHistory<T> fileSystemFactoryStorageHistory;

    private T initialFactory;
    private Path basePath;
    private Path currentFactoryPath;
    private Path currentFactoryPathMetadata;
    private final SimpleObjectMapper objectMapper=ObjectMapperBuilder.build();
    private final Class<T> rootClass;

    public FileSystemFactoryStorage(Path basePath, T defaultFactory, Class<T> rootClass, FileSystemFactoryStorageHistory<T> fileSystemFactoryStorageHistory){
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
    public void updateCurrentFactory(T factoryRoot, String user) {
        FactoryAndStorageMetadata<T> currentFactory = getCurrentFactory();
        fileSystemFactoryStorageHistory.updateHistory(currentFactory.metadata,currentFactory.root);

        String newId = UUID.randomUUID().toString();
        StoredFactoryMetadata metadata = new StoredFactoryMetadata();
        metadata.id=newId;
        metadata.baseVersionId= currentFactory.metadata.id;
        metadata.user=user;

        objectMapper.writeValue(currentFactoryPath.toFile(),factoryRoot);
        objectMapper.writeValue(currentFactoryPathMetadata.toFile(),metadata);
    }

    @Override
    public void loadInitialFactory() {
        fileSystemFactoryStorageHistory.initFromFileSystem();
        if (Files.exists(currentFactoryPath)){
            objectMapper.readValue(currentFactoryPath.toFile(),rootClass);
            objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class);
        } else {
            objectMapper.writeValue(currentFactoryPath.toFile(),initialFactory);
            StoredFactoryMetadata metadata = new StoredFactoryMetadata();
            String newId = UUID.randomUUID().toString();
            metadata.id=newId;
            metadata.baseVersionId= newId;
            metadata.user="System";
            objectMapper.writeValue(currentFactoryPathMetadata.toFile(), metadata);
        }
    }
}
