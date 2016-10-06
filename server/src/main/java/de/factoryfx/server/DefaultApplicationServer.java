package de.factoryfx.server;

import java.util.Collection;
import java.util.Locale;

import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class DefaultApplicationServer<L,V,T extends FactoryBase<L,V>> implements ApplicationServer<L,V,T> {

    private final FactoryManager<L,V,T> factoryManager;
    private final FactoryStorage<L,V,T> factoryStorage;

    public DefaultApplicationServer(FactoryManager<L,V,T> factoryManager, FactoryStorage<L,V,T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    @Override
    public MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update, Locale locale) {
        T commonVersion = factoryStorage.getHistoryFactory(update.metadata.baseVersionId);
        MergeDiff mergeDiff = factoryManager.update(commonVersion, update.root, locale);
        if (mergeDiff.hasNoConflicts()){
            FactoryAndStorageMetadata<T> copy = new FactoryAndStorageMetadata<T>(factoryManager.getCurrentFactory().copy(),update.metadata);
            factoryStorage.updateCurrentFactory(copy);
        }
        return mergeDiff;
    }

    @Override
    public MergeDiff simulateUpdateCurrentFactory(T updateFactory, String baseVersionId, Locale locale){
        T commonVersion = factoryStorage.getHistoryFactory(baseVersionId);
        return factoryManager.simulateUpdate(commonVersion , updateFactory, locale);
    }

    @Override
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        return factoryStorage.getCurrentFactory();
    }

    @Override
    public FactoryAndStorageMetadata<T> getPrepareNewFactory() {
        return factoryStorage.getPrepareNewFactory();
    }

    @Override
    public T getHistoryFactory(String id) {
        return factoryStorage.getHistoryFactory(id);
    }

    @Override
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return factoryStorage.getHistoryFactoryList();
    }

    @Override
    public void start() {
        factoryStorage.loadInitialFactory();
        factoryManager.start(factoryStorage.getCurrentFactory().root);
    }

    @Override
    public void stop() {
        factoryManager.stop();
    }

    @Override
    public V query(V visitor) {
        return factoryManager.query(visitor);
    }

}
