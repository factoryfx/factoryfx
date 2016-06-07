package de.factoryfx.server;

import java.util.Collection;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.datastorage.FactoryStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;

public class DefaultApplicationServer<T extends FactoryBase<? extends LiveObject, T>> implements ApplicationServer<T> {

    private final FactoryManager<T> factoryManager;
    private final FactoryStorage<T> factoryStorage;

    public DefaultApplicationServer(FactoryManager<T> factoryManager, FactoryStorage<T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    @Override
    public MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactoryRequest) {
        T commonVersion = factoryStorage.getHistoryFactory(updateFactoryRequest.baseVersionId).root;
        MergeDiff mergeDiff = factoryManager.update(commonVersion, updateFactoryRequest.root);
        if (mergeDiff.hasNoConflicts()){
            factoryStorage.updateCurrentFactory(factoryManager.getCurrentFactory());
        }
        return mergeDiff;
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

}
