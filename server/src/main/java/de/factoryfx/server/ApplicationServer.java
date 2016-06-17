package de.factoryfx.server;

import java.util.Collection;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;


public interface ApplicationServer<V,T extends FactoryBase<? extends LiveObject<V>, T>> {
    MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactory);
    ApplicationFactoryMetadata<T> getCurrentFactory();
    ApplicationFactoryMetadata<T> getHistoryFactory(String id);
    Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList();
    void start();
    void stop();

    V query(V visitor);
}
