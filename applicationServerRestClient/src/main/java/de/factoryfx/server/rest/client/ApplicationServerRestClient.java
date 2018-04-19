package de.factoryfx.server.rest.client;

import java.util.*;

import de.factoryfx.data.merge.AttributeDiffInfo;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.rest.CheckUserResponse;
import de.factoryfx.server.rest.DiffForFactoryResponse;
import de.factoryfx.server.rest.UpdateCurrentFactoryRequest;
import de.factoryfx.server.rest.UserAwareRequest;
import de.factoryfx.server.rest.UserLocaleResponse;


//TODO use rest proxy client
public class ApplicationServerRestClient<V, R extends FactoryBase<?,V,R>> {

    private final Class<R> factoryRootClass;
    private final RestClient restClient;
    private final String user;
    private final String passwordHash;


    public ApplicationServerRestClient(RestClient restClient, Class<R> factoryRootClass, String user, String passwordHash) {
        this.restClient = restClient;
        this.factoryRootClass = factoryRootClass;
        this.user=user;
        this.passwordHash=passwordHash;
    }

    public FactoryUpdateLog updateCurrentFactory(DataAndNewMetadata<R> update, String comment) {
        final UpdateCurrentFactoryRequest updateCurrentFactoryRequest = new UpdateCurrentFactoryRequest();
        updateCurrentFactoryRequest.comment=comment;
        updateCurrentFactoryRequest.factoryUpdate=update;
        return restClient.post("updateCurrentFactory", new UserAwareRequest<>(user,passwordHash,updateCurrentFactoryRequest), FactoryUpdateLog.class);
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataAndNewMetadata<R> update) {
        return restClient.post("simulateUpdateCurrentFactory", new UserAwareRequest<>(user,passwordHash,update), MergeDiffInfo.class);
    }

    /**
     * @see DataStorage#getPrepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    @SuppressWarnings("unchecked")
    public DataAndNewMetadata<R> prepareNewFactory() {
        DataAndNewMetadata<R> currentFactory = restClient.post("prepareNewFactory",new UserAwareRequest<Void>(user,passwordHash,null), DataAndNewMetadata.class);
        return new DataAndNewMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> getDiff(StoredDataMetadata historyEntry) {
        return restClient.post("diff", new UserAwareRequest<>(user, passwordHash, historyEntry), MergeDiffInfo.class);
    }


    public R getHistoryFactory(String id) {
        return restClient.post("historyFactory",new UserAwareRequest<>(user,passwordHash,id), factoryRootClass).internal().prepareUsableCopy();
    }

    static final Class<? extends ArrayList<StoredDataMetadata>> collectionOfStoredFactoryMetadataClass = new ArrayList<StoredDataMetadata>() {}.getClass();
    public Collection<StoredDataMetadata> getHistoryFactoryList() {
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

    public FactoryUpdateLog revert(StoredDataMetadata historyFactory) {
        return restClient.post("revert",new UserAwareRequest<>(user,passwordHash,historyFactory), FactoryUpdateLog.class);
    }

    public List<AttributeDiffInfo> getSingleFactoryHistory(String factoryId) {
        return restClient.post("diffForFactory",new UserAwareRequest<>(user,passwordHash,factoryId), DiffForFactoryResponse.class).diffs;
    }

}
