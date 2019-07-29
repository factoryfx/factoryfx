package io.github.factoryfx.javafx.factoryviewmanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.RawFactoryDataAndMetadata;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import javafx.application.Platform;

/**
 * @param <R> Root
 */
public class FactoryEditManager<R extends FactoryBase<?,R>> {
    private final MicroserviceRestClient<R> client;
    private final List<FactoryRootChangeListener<R>> listeners= new ArrayList<>();
    private final MigrationManager<R> migrationManager;

    public FactoryEditManager(MicroserviceRestClient<R> client, MigrationManager<R> migrationManager) {
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
        try {
            Files.writeString(target, migrationManager.writeRawFactoryDataAndMetadata(loadedRoot.root,loadedRoot.createUpdateStoredDataMetadata(null,null)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromFile(Path from) {
        try {
            RawFactoryDataAndMetadata wrapper = migrationManager.readRawFactoryDataAndMetadata(Files.readString(from));

            R serverFactory = migrationManager.read(wrapper.root,wrapper.metadata.dataStorageMetadataDictionary);

            DataUpdate<R> previousRoot=loadedRoot;

            DataUpdate<R> update = client.prepareNewFactory();
            loadedRoot=new DataUpdate<>(serverFactory,update.user,update.comment,update.baseVersionId);
            this.save("reloaded from file: "+from.toFile().getAbsolutePath());

            updateNotify(loadedRoot, previousRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public MergeDiffInfo<R> simulateUpdateCurrentFactory() {
        return client.simulateUpdateCurrentFactory(loadedRoot);
    }
}
