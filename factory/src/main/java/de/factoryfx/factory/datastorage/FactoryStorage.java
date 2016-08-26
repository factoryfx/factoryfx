package de.factoryfx.factory.datastorage;

import java.util.Collection;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public interface FactoryStorage<T extends FactoryBase<? extends LiveObject<?>, T>> {

    T getHistoryFactory(String id);

    Collection<StoredFactoryMetadata> getHistoryFactoryList();

    FactoryAndStorageMetadata<T> getCurrentFactory();

    FactoryAndStorageMetadata<T> getPrepareNewFactory();

    void updateCurrentFactory(FactoryAndStorageMetadata<T> update);

    /**at Application start load current Factory*/
    void loadInitialFactory();
}
