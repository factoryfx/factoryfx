package de.factoryfx.server;

import java.util.Collection;
import java.util.Locale;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.merge.MergeDiff;


public interface ApplicationServer<V,T extends FactoryBase<? extends LiveObject<V>, T>> {
    MergeDiff updateCurrentFactory(T updateFactory, String baseVersionId, Locale locale, String user);
    MergeDiff simulateUpdateCurrentFactory(T updateFactory, String baseVersionId, Locale locale);
    FactoryAndStorageMetadata<T> getCurrentFactory();
    T getHistoryFactory(String id);
    Collection<StoredFactoryMetadata> getHistoryFactoryList();
    void start();
    void stop();

    V query(V visitor);
}
