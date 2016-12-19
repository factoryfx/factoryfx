package de.factoryfx.server;

import java.time.LocalDateTime;
import java.util.Collection;

import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;


public class ApplicationServer<L,V,T extends FactoryBase<L,V>> {
    private final FactoryManager<L,V,T> factoryManager;
    private final FactoryStorage<L,V,T> factoryStorage;

    public ApplicationServer(FactoryManager<L,V,T> factoryManager, FactoryStorage<L,V,T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    public MergeDiff getDiff(StoredFactoryMetadata storedFactoryMetadata) {
        T historyFactory = getHistoryFactory(storedFactoryMetadata.id);
        T historyFactoryPrevious = getPreviousHistoryFactory(storedFactoryMetadata.id);
        return new DataMerger(historyFactoryPrevious,historyFactoryPrevious,historyFactory).createMergeResult();
    }

    public MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        T commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        MergeDiff mergeDiff = factoryManager.update(commonVersion, update.root);
        if (mergeDiff.hasNoConflicts()){
            update.metadata.creationTime= LocalDateTime.now();
            FactoryAndStorageMetadata<T> copy = new FactoryAndStorageMetadata<>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy);
        }
        return mergeDiff;
    }

    public MergeDiff simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> possibleUpdate){
        T commonVersion = factoryStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , possibleUpdate.root);
    }

    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        return factoryStorage.getCurrentFactory();
    }

    /** creates a new factory which is ready for editing mainly assign the right ids*/
    public FactoryAndStorageMetadata<T> getPrepareNewFactory() {
        return factoryStorage.getPrepareNewFactory();
    }

    public T getHistoryFactory(String id) {
        T historyFactory = factoryStorage.getHistoryFactory(id);
        return historyFactory;
    }

    public T getPreviousHistoryFactory(String id) {
        T historyFactory = factoryStorage.getPreviousHistoryFactory(id);
        return historyFactory;
    }

    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return factoryStorage.getHistoryFactoryList();
    }

    public void start() {
        factoryStorage.loadInitialFactory();
        factoryManager.start(factoryStorage.getCurrentFactory().root);
    }

    public void stop() {
        factoryManager.stop();
    }

    public V query(V visitor) {
        return factoryManager.query(visitor);
    }
}
