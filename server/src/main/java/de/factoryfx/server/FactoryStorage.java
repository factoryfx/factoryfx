package de.factoryfx.server;

import java.util.List;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public interface FactoryStorage<T extends FactoryBase<? extends LiveObject, T>> {

    ApplicationFactoriesMetadata<T> getHistoryFactory(String id);

    List<ApplicationFactoriesMetadata<T>> getHistoryFactoryList();

    ApplicationFactoriesMetadata<T> getCurrentFactory();

    ApplicationFactoriesMetadata<T> updateCurrentFactory(T factoryRoot);
}
