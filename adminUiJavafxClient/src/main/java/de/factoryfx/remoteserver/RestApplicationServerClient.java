package de.factoryfx.remoteserver;

import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class RestApplicationServerClient<V,T extends FactoryBase<?,V>> {

    private final Client client;
    private final URI baseURI;
    private final Class<? extends T> configurationRootClass;

    public RestApplicationServerClient(Client client, URI baseURI, Class<? extends T> configurationRootClass) {
        this.client = client;
        this.baseURI = baseURI;
        this.configurationRootClass = configurationRootClass;
    }

    public MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return post("updateCurrentFactory", update, MergeDiff.class);
    }

    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        return get("currentFactory", FactoryAndStorageMetadata.class);
    }


    public T getHistoryFactory(String id) {
        return get("historyFactory", configurationRootClass);
    }

    static final Class<? extends ArrayList<StoredFactoryMetadata>> collectionOfStoredFactoryMetadataClass = new ArrayList<StoredFactoryMetadata>() {}.getClass();
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return get("historyFactoryList", collectionOfStoredFactoryMetadataClass);
    }

    public void start() {
        get("start");
    }

    public void stop() {
        get("stop");
    }


    public V query(V visitor) {
        return post("query",visitor,(Class<? extends V>)visitor.getClass());
    }

    private <R> R post(String subPath, Object entity, Class<R> returnType) {
        return client.target(baseURI.resolve(subPath)).request().post(Entity.json(entity)).readEntity(returnType);
    }

    private <R> R get(String subPath, Class<R> returnType) {
        return client.target(baseURI.resolve(subPath)).request().get().readEntity(returnType);
    }

    private Object get(String subPath) {
        return client.target(baseURI.resolve(subPath)).request().get().getEntity();
    }



}
