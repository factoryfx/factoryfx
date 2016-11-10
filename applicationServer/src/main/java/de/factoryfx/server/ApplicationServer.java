package de.factoryfx.server;

import java.util.Collection;

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

    public MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        T commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        commonVersion.internal().prepareUsage();
        MergeDiff mergeDiff = factoryManager.update(commonVersion, update.root);
        if (mergeDiff.hasNoConflicts()){
            FactoryAndStorageMetadata<T> copy = new FactoryAndStorageMetadata<T>(factoryManager.getCurrentFactory().internal().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy);
        }
        return mergeDiff;
    }

    public MergeDiff simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> possibleUpdate){
        T commonVersion = factoryStorage.getHistoryFactory(possibleUpdate.metadata.baseVersionId);
        commonVersion.internal().prepareUsage();
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
        final T historyFactory = factoryStorage.getHistoryFactory(id);
        historyFactory.internal().prepareUsage();
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
