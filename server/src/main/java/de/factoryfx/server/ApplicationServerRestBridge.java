package de.factoryfx.server;

import java.util.Collection;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;

public class ApplicationServerRestBridge<T extends FactoryBase<? extends LiveObject<V>, T>,V> implements ApplicationServer<V,T> {

    @Override
    public MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactoryRequest) {
        return null;
    }

    @Override
    public ApplicationFactoryMetadata<T> getCurrentFactory() {
        return null;
    }

    @Override
    public ApplicationFactoryMetadata<T> getHistoryFactory(String id) {
        return null;
    }

    @Override
    public Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public V query(V visitor) {
        return null;
    }
}
