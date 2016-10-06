package de.factoryfx.server;

import java.util.Collection;
import java.util.Locale;

import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;


public interface ApplicationServer<L,V,T extends FactoryBase<L,V>> {
    MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update, Locale locale);
    MergeDiff simulateUpdateCurrentFactory(T updateFactory, String baseVersionId, Locale locale);
    FactoryAndStorageMetadata<T> getCurrentFactory();
    //** creates a new factory which is ready for edditing mainly assign the right ids*/
    FactoryAndStorageMetadata<T> getPrepareNewFactory();
    T getHistoryFactory(String id);
    Collection<StoredFactoryMetadata> getHistoryFactoryList();
    void start();
    void stop();

    V query(V visitor);
}
