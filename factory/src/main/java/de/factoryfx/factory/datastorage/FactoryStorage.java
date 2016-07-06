package de.factoryfx.factory.datastorage;

import java.util.Collection;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public interface FactoryStorage<T extends FactoryBase<? extends LiveObject, T>> {

    ApplicationFactoryMetadata<T> getHistoryFactory(String id);

    Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList();

    ApplicationFactoryMetadata<T> getCurrentFactory();

    ApplicationFactoryMetadata<T> updateCurrentFactory(T factoryRoot);

    /**at Application start load current Factory*/
    void loadInitialFactory();
}
