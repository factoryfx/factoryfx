package de.factoryfx.server;

import java.util.Collection;
import java.util.Locale;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.datastorage.FactoryStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;

public class DefaultApplicationServer<V,T extends FactoryBase<? extends LiveObject<V>, T>> implements ApplicationServer<V,T> {

    private final FactoryManager<T,V> factoryManager;
    private final FactoryStorage<T> factoryStorage;

    public DefaultApplicationServer(FactoryManager<T,V> factoryManager, FactoryStorage<T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    @Override
    public MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactory, Locale locale) {
        T commonVersion = factoryStorage.getHistoryFactory(updateFactory.baseVersionId).root;
        MergeDiff mergeDiff = factoryManager.update(commonVersion, updateFactory.root,locale);
        if (mergeDiff.hasNoConflicts()){
            factoryStorage.updateCurrentFactory(factoryManager.getCurrentFactory());
        }
        return mergeDiff;
    }

    @Override
    public MergeDiff simulateUpdateCurrentFactory(ApplicationFactoryMetadata<T> updateFactory, Locale locale){
        T commonVersion = factoryStorage.getHistoryFactory(updateFactory.baseVersionId).root;
        return factoryManager.simulateUpdate(commonVersion , updateFactory.root, locale);
    }

    @Override
    public ApplicationFactoryMetadata<T> getCurrentFactory() {
        return factoryStorage.getCurrentFactory();
    }

    @Override
    public ApplicationFactoryMetadata<T> getHistoryFactory(String id) {
        return factoryStorage.getHistoryFactory(id);
    }

    @Override
    public Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList() {
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
