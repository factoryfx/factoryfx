package de.factoryfx.factory.datastorage;

import java.util.Collection;

import de.factoryfx.factory.FactoryBase;

public interface FactoryStorage<L,V,T extends FactoryBase<L,V>> {

    T getHistoryFactory(String id);

    Collection<StoredFactoryMetadata> getHistoryFactoryList();

    FactoryAndStorageMetadata<T> getCurrentFactory();

    /** prepare a new Factory which could we an update. mainly give it a new valid Id and the correct baseVersionId*/
    FactoryAndStorageMetadata<T> getPrepareNewFactory();

    /** updateCurrentFactory and history*/
    void updateCurrentFactory(FactoryAndStorageMetadata<T> update);

    /**at Application start load current Factory*/
    void loadInitialFactory();
}
