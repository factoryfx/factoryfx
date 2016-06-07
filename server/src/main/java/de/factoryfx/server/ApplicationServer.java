package de.factoryfx.server;

import java.util.Collection;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;


public interface ApplicationServer<T extends FactoryBase<? extends LiveObject, T>,V> {
    MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactoryRequest);
    ApplicationFactoryMetadata<T> getCurrentFactory();
    ApplicationFactoryMetadata<T> getHistoryFactory(String id);
    Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList();
    void start();
    void stop();

    void query(V visitor);
}
