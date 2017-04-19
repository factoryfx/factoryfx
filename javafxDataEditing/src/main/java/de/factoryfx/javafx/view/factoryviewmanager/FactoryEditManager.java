package de.factoryfx.javafx.view.factoryviewmanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactorySerialisationManager;
import de.factoryfx.factory.datastorage.NewFactoryMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import javafx.application.Platform;

public class FactoryEditManager<V,R extends FactoryBase<?,V>> {
    private final ApplicationServerRestClient<V,R> client;
    private final List<FactoryRootChangeListener> listeners= new ArrayList<>();
    private final FactorySerialisationManager<R> factorySerialisationManager;

    public FactoryEditManager(ApplicationServerRestClient<V, R> client, FactorySerialisationManager<R> factorySerialisationManager) {
        this.client = client;
        this.factorySerialisationManager = factorySerialisationManager;
    }

    public void registerListener(FactoryRootChangeListener listener){
        listeners.add(listener);
    }

    public void removeListener(FactoryRootChangeListener listener){
        listeners.remove(listener);
    }

    Optional<FactoryAndNewMetadata<R>> loadedRoot = Optional.empty();
    public void load(){
        FactoryAndNewMetadata<R> currentFactory = client.prepareNewFactory();
        Optional<R> previousRoot=getLoadedFactory();
        loadedRoot = Optional.of(currentFactory);

        updateNotify(currentFactory, previousRoot);
    }

    private void updateNotify(FactoryAndNewMetadata<R> currentFactory, Optional<R> previousRoot) {
        Platform.runLater(() -> {
            for (FactoryRootChangeListener listener: listeners){
                listener.update(previousRoot,currentFactory.root);
            }
        });
    }

    public Optional<R> getLoadedFactory(){
        if (loadedRoot.isPresent()){
            return  Optional.of(loadedRoot.get().root);
        }
        return Optional.empty();
    }

    public void reset() {
        load();
    }

    public FactoryUpdateLog save(String comment) {
        final FactoryAndNewMetadata<R> update = loadedRoot.get();
        final FactoryUpdateLog factoryLog = client.updateCurrentFactory(update,comment);
        load();//to edit the newly merged data
        return factoryLog;
    }

    public void saveToFile(Path target) {
        FactoryAndStringifyedStorageMetadata factoryAndStringifyedStorageMetadata = new FactoryAndStringifyedStorageMetadata();
        factoryAndStringifyedStorageMetadata.metadata=loadedRoot.get().metadata;
        factoryAndStringifyedStorageMetadata.root= ObjectMapperBuilder.build().writeValueAsString(loadedRoot.get().root);
        ObjectMapperBuilder.build().writeValue(target.toFile(),factoryAndStringifyedStorageMetadata);
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(Path target) {
        Optional<R> previousRoot=getLoadedFactory();
        final FactoryAndStringifyedStorageMetadata value = ObjectMapperBuilder.build().readValue(target.toFile(), FactoryAndStringifyedStorageMetadata.class);
        R serverFactory = factorySerialisationManager.read(value.root, value.metadata.dataModelVersion);
        serverFactory = serverFactory.internal().prepareUsableCopy();
        loadedRoot=Optional.of(new FactoryAndNewMetadata<>(serverFactory,value.metadata));
        updateNotify(loadedRoot.get(), previousRoot);
    }

    private static class FactoryAndStringifyedStorageMetadata{
        public String root;
        public NewFactoryMetadata metadata;
    }

    public MergeDiffInfo simulateUpdateCurrentFactory() {
        final FactoryAndNewMetadata<R> update = loadedRoot.get();
        final MergeDiffInfo mergeDiffInfo = client.simulateUpdateCurrentFactory(update);
        return mergeDiffInfo;
    }
}
