package de.factoryfx.factory.datastorage.postgres;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static java.nio.file.Files.readAllBytes;

public class SqlDatabaseFactoryStorage<V,L,R extends FactoryBase<L,V>> implements FactoryStorage<V,L, R> {
//    private final SqlDatabaseFactoryStorageHistory<V,L, R> fileSystemFactoryStorageHistory;

    private R initialFactory;
    private Path currentFactoryPath;
    private Path currentFactoryPathMetadata;
//    private final FactorySerialisationManager<R> factorySerialisationManager;

    public SqlDatabaseFactoryStorage(Path basePath, R defaultFactory, FactorySerialisationManager<R> factorySerialisationManager, SqlDatabaseFactoryStorageHistory<V,L, R> fileSystemFactoryStorageHistory){
        this.initialFactory=defaultFactory;
        if (!Files.exists(basePath)){
            throw new IllegalArgumentException("path don't exists:"+basePath);
        }
        currentFactoryPath= Paths.get(basePath.toString()+"/currentFactory.json");
        currentFactoryPathMetadata= Paths.get(basePath.toString()+"/currentFactory_metadata.json");
//        this.fileSystemFactoryStorageHistory=fileSystemFactoryStorageHistory;
//        this.factorySerialisationManager=factorySerialisationManager;
    }

    public SqlDatabaseFactoryStorage(Path basePath, R defaultFactory, FactorySerialisationManager<R> factorySerialisationManager){

//        this(basePath,defaultFactory,factorySerialisationManager,new SqlDatabaseFactoryStorageHistory<>(basePath,factorySerialisationManager));
    }


    @Override
    public R getHistoryFactory(String id) {
        return null;
//        return fileSystemFactoryStorageHistory.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return null;
//        return fileSystemFactoryStorageHistory.getHistoryFactoryList();
    }

    @Override
    public FactoryAndStoredMetadata<R> getCurrentFactory() {
        return null;
//        StoredFactoryMetadata storedFactoryMetadata = factorySerialisationManager.readStoredFactoryMetadata(readFile(currentFactoryPathMetadata));
//        return new FactoryAndStoredMetadata<>(factorySerialisationManager.read(readFile(currentFactoryPath),storedFactoryMetadata.dataModelVersion),storedFactoryMetadata);
    }

    @Override
    public void updateCurrentFactory(FactoryAndNewMetadata<R> update, String user, String comment) {
        final StoredFactoryMetadata storedFactoryMetadata = new StoredFactoryMetadata();
        storedFactoryMetadata.creationTime= LocalDateTime.now();
        storedFactoryMetadata.id= UUID.randomUUID().toString();
        storedFactoryMetadata.user=user;
        storedFactoryMetadata.comment=comment;
        storedFactoryMetadata.baseVersionId=update.metadata.baseVersionId;
        storedFactoryMetadata.dataModelVersion=update.metadata.dataModelVersion;
        final FactoryAndStoredMetadata<R> updateData = new FactoryAndStoredMetadata<>(update.root, storedFactoryMetadata);

//        writeFile(currentFactoryPath,factorySerialisationManager.write(updateData.root));
//        writeFile(currentFactoryPathMetadata,factorySerialisationManager.writeStorageMetadata(updateData.metadata));
//        fileSystemFactoryStorageHistory.updateHistory(updateData.metadata,updateData.root);
    }

    @Override
    public FactoryAndNewMetadata<R> getPrepareNewFactory(){
        NewFactoryMetadata metadata = new NewFactoryMetadata();
        metadata.baseVersionId=getCurrentFactory().metadata.id;
//        factorySerialisationManager.prepareNewFactoryMetadata(metadata);
        return new FactoryAndNewMetadata<>(getCurrentFactory().root,metadata);
    }


    @Override
    public void loadInitialFactory() {
//        fileSystemFactoryStorageHistory.initFromFileSystem();
        if (Files.exists(currentFactoryPath)){
//            objectMapper.readValue(currentFactoryPath.toFile(),rootClass);
//            objectMapper.readValue(currentFactoryPathMetadata.toFile(),StoredFactoryMetadata.class);
        } else {
            NewFactoryMetadata metadata = new NewFactoryMetadata();
            metadata.baseVersionId= UUID.randomUUID().toString();
            FactoryAndNewMetadata<R> initialFactoryAndStorageMetadata = new FactoryAndNewMetadata<>(initialFactory,metadata);
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
