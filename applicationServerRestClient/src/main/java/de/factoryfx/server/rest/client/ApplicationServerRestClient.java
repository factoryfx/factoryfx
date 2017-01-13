package de.factoryfx.server.rest.client;

import java.util.ArrayList;
import java.util.Collection;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.server.rest.CheckUserResponse;
import de.factoryfx.server.rest.KeyResponse;
import de.factoryfx.server.rest.UserAwareRequest;

public class ApplicationServerRestClient<V,T extends FactoryBase<?,V>> {


    private final Class<T> factoryRootClass;
    private final RestClient restClient;
    private final String user;
    private final String passwordHash;


    public ApplicationServerRestClient(RestClient restClient, Class<T> factoryRootClass, String user, String passwordHash) {
        this.restClient = restClient;
        this.factoryRootClass = factoryRootClass;
        this.user=user;
        this.passwordHash=passwordHash;
    }

    public MergeDiffInfo updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return restClient.post("updateCurrentFactory", new UserAwareRequest<>(user,passwordHash,update), MergeDiffInfo.class);
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return restClient.post("simulateUpdateCurrentFactory", new UserAwareRequest<>(user,passwordHash,update), MergeDiffInfo.class);
    }

    /** @see FactoryStorage#getPrepareNewFactory() */
    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<T> prepareNewFactory() {
        FactoryAndStorageMetadata<T> currentFactory = restClient.post("prepareNewFactory",new UserAwareRequest<Void>(user,passwordHash,null), FactoryAndStorageMetadata.class);
        return new FactoryAndStorageMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }

    public MergeDiffInfo getDiff(StoredFactoryMetadata historyEntry) {
        return restClient.post("diff", new UserAwareRequest<>(user,passwordHash,historyEntry), MergeDiffInfo.class);
    }


    public T getHistoryFactory(String id) {
        return restClient.post("historyFactory",new UserAwareRequest<String>(user,passwordHash,id), factoryRootClass).internal().prepareUsableCopy();
    }

    static final Class<? extends ArrayList<StoredFactoryMetadata>> collectionOfStoredFactoryMetadataClass = new ArrayList<StoredFactoryMetadata>() {}.getClass();
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return restClient.post("historyFactoryList",new UserAwareRequest<Void>(user,passwordHash,null), collectionOfStoredFactoryMetadataClass);
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor) {
        return restClient.post("query",new UserAwareRequest<>(user,passwordHash,visitor),(Class<? extends V>)visitor.getClass());
    }

    public boolean checkUser() {
        CheckUserResponse response = restClient.post("checkUser",new UserAwareRequest<>(user,passwordHash,null), CheckUserResponse.class);
        return response.valid;
    }

    public String getUserKey() {
        KeyResponse response = restClient.post("userKey",new UserAwareRequest<>(user,passwordHash,null), KeyResponse.class);
        return response.key;
    }
}
