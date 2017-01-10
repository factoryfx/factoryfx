package de.factoryfx.server.rest.client;

import java.util.ArrayList;
import java.util.Collection;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

public class ApplicationServerRestClient<V,T extends FactoryBase<?,V>> {


    private final Class<T> factoryRootClass;
    private final RestClient restClient;

    public ApplicationServerRestClient(RestClient restClient, Class<T> factoryRootClass) {
        this.restClient = restClient;
        this.factoryRootClass = factoryRootClass;
    }

    public MergeDiffInfo updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return restClient.post("updateCurrentFactory", update, MergeDiffInfo.class);
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return restClient.post("simulateUpdateCurrentFactory", update, MergeDiffInfo.class);
    }


    /** <b>don't use this if you want to change the factorydata.</b> use {@link #prepareNewFactory()} instead*/
    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        FactoryAndStorageMetadata<T> currentFactory = restClient.get("currentFactory", FactoryAndStorageMetadata.class);
        return new FactoryAndStorageMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }


    /** @see FactoryStorage#getPrepareNewFactory() */
    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<T> prepareNewFactory() {
        FactoryAndStorageMetadata<T> currentFactory = restClient.get("prepareNewFactory", FactoryAndStorageMetadata.class);
        return new FactoryAndStorageMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }

    public MergeDiffInfo getDiff(StoredFactoryMetadata historyEntry) {
        return restClient.post("diff", historyEntry, MergeDiffInfo.class);
    }


    public T getHistoryFactory(String id) {
        return restClient.get("historyFactory", factoryRootClass).internal().prepareUsableCopy();
    }

    static final Class<? extends ArrayList<StoredFactoryMetadata>> collectionOfStoredFactoryMetadataClass = new ArrayList<StoredFactoryMetadata>() {}.getClass();
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return restClient.get("historyFactoryList", collectionOfStoredFactoryMetadataClass);
    }

    public void start() {
        restClient.get("start");
    }

    public void stop() {
        restClient.get("stop");
    }


    @SuppressWarnings("unchecked")
    public V query(V visitor) {
        return restClient.post("query",visitor,(Class<? extends V>)visitor.getClass());
    }



}
