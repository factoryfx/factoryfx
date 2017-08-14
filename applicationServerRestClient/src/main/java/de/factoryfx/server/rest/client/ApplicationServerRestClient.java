package de.factoryfx.server.rest.client;

import java.util.*;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.rest.CheckUserResponse;
import de.factoryfx.server.rest.DiffForFactoryResponse;
import de.factoryfx.server.rest.UpdateCurrentFactoryRequest;
import de.factoryfx.server.rest.UserAwareRequest;
import de.factoryfx.server.rest.UserLocaleResponse;

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

    public FactoryUpdateLog updateCurrentFactory(FactoryAndNewMetadata<T> update, String comment) {
        final UpdateCurrentFactoryRequest updateCurrentFactoryRequest = new UpdateCurrentFactoryRequest();
        updateCurrentFactoryRequest.comment=comment;
        updateCurrentFactoryRequest.factoryUpdate=update;
        return restClient.post("updateCurrentFactory", new UserAwareRequest<>(user,passwordHash,updateCurrentFactoryRequest), FactoryUpdateLog.class);
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndNewMetadata<T> update) {
        return restClient.post("simulateUpdateCurrentFactory", new UserAwareRequest<>(user,passwordHash,update), MergeDiffInfo.class);
    }

    /**
     * @see FactoryStorage#getPrepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    @SuppressWarnings("unchecked")
    public FactoryAndNewMetadata<T> prepareNewFactory() {
        FactoryAndNewMetadata<T> currentFactory = restClient.post("prepareNewFactory",new UserAwareRequest<Void>(user,passwordHash,null), FactoryAndNewMetadata.class);
        return new FactoryAndNewMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }

    public MergeDiffInfo getDiff(StoredFactoryMetadata historyEntry) {
        return restClient.post("diff", new UserAwareRequest<>(user, passwordHash, historyEntry), MergeDiffInfo.class);
    }


    public T getHistoryFactory(String id) {
        return restClient.post("historyFactory",new UserAwareRequest<>(user,passwordHash,id), factoryRootClass).internal().prepareUsableCopy();
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

    public Locale getLocale() {
        UserLocaleResponse response = restClient.post("userLocale",new UserAwareRequest<>(user,passwordHash,null), UserLocaleResponse.class);
        return response.locale;
    }

    public FactoryUpdateLog revert(StoredFactoryMetadata historyFactory) {
        return restClient.post("revert",new UserAwareRequest<>(user,passwordHash,historyFactory), FactoryUpdateLog.class);
    }

    public List<AttributeDiffInfo> getSingleFactoryHistory(String factoryId) {
        return restClient.post("diffForFactory",new UserAwareRequest<>(user,passwordHash,factoryId), DiffForFactoryResponse.class).diffs;
    }

}
