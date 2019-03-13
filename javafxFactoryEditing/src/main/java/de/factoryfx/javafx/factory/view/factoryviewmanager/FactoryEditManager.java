package de.factoryfx.javafx.factory.view.factoryviewmanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.migration.MigrationManager;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import javafx.application.Platform;

/**
 * @param <V> Visitor
 * @param <R> Root
 * @param <S> Summary Data for factory history
 */
public class FactoryEditManager<V,R extends FactoryBase<?,V,R>,S> {
    private final MicroserviceRestClient<V,R,S> client;
    private final List<FactoryRootChangeListener<R>> listeners= new ArrayList<>();
    private final MigrationManager<R,S> migrationManager;

    public FactoryEditManager(MicroserviceRestClient<V, R, S> client, MigrationManager<R,S> migrationManager) {
        this.client = client;
        this.migrationManager = migrationManager;
    }

    public void registerListener(FactoryRootChangeListener<R> listener){
        listeners.add(listener);
    }

    public void removeListener(FactoryRootChangeListener listener){
        listeners.remove(listener);
    }

    DataUpdate<R> loadedRoot;
    public void load(){
        DataUpdate<R> currentFactory = client.prepareNewFactory();
        DataUpdate<R> previousRoot=loadedRoot;
        loadedRoot = currentFactory;

        updateNotify(currentFactory, previousRoot);
    }

    private void updateNotify(DataUpdate<R> currentFactory, DataUpdate<R> previousRoot) {
        runLaterExecuter.accept(() -> {
            for (FactoryRootChangeListener<R> listener: listeners){
                listener.update(Optional.ofNullable(previousRoot).map((p)->p.root),currentFactory.root);
            }
        });
    }

    //for testability, avoid Toolkit not initialized
    Consumer<Runnable> runLaterExecuter= Platform::runLater;


    public Optional<R> getLoadedFactory(){
        if (loadedRoot==null){
            return Optional.empty();
        }
        return Optional.of(loadedRoot.root);
    }

    public void reset() {
        load();
    }

    public FactoryUpdateLog save(String comment) {
        final FactoryUpdateLog factoryLog = client.updateCurrentFactory(loadedRoot,comment);
        if (factoryLog.successfullyMerged()) {
            load();//to edit the newly merged data
        }
        if (factoryLog.failedUpdate()) {
            for (int i = 0; i < 30; i++) {
                try {
                    load();
                    break;
                } catch (Exception ex) {
                    try {
                        Thread.sleep(1000);//wait fo the server to restart
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return factoryLog;
    }

    public void saveToFile(Path target) {
        RawFactoryDataAndMetadata<S> rawFactoryDataAndMetadata = new RawFactoryDataAndMetadata<>();
        rawFactoryDataAndMetadata.metadata=loadedRoot.createUpdateStoredDataMetadata(null);
        rawFactoryDataAndMetadata.root= ObjectMapperBuilder.build().writeValueAsTree(loadedRoot.root);
        ObjectMapperBuilder.build().writeValue(target.toFile(), rawFactoryDataAndMetadata);
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(Path from) {
        RawFactoryDataAndMetadata<S> wrapper = (RawFactoryDataAndMetadata<S>)ObjectMapperBuilder.build().readValue(from.toFile(), RawFactoryDataAndMetadata.class);
        R serverFactory = migrationManager.read(wrapper.root,wrapper.metadata.dataStorageMetadataDictionary);

        DataUpdate<R> previousRoot=loadedRoot;

        DataUpdate<R> update = client.prepareNewFactory();
        loadedRoot=new DataUpdate<>(serverFactory,update.user,update.comment,update.baseVersionId);
        this.save("reloaded from file: "+from.toFile().getAbsolutePath());

        updateNotify(loadedRoot, previousRoot);
    }

    private static class RawFactoryDataAndMetadata<S>{
        public JsonNode root;
        public StoredDataMetadata<S> metadata;
    }

    public MergeDiffInfo<R> simulateUpdateCurrentFactory() {
        return client.simulateUpdateCurrentFactory(loadedRoot);
    }
}
