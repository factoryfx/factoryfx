package de.factoryfx.microservice.rest.client;

import java.util.*;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;

import de.factoryfx.microservice.common.*;


/**
 *
 * @param <V> Visitor
 * @param <R> Root factory
 * @param <S> History summary
 */
public class MicroserviceRestClient<V, R extends FactoryBase<?,V,R>,S> {

    private final Class<R> factoryRootClass;
    private final MicroserviceResourceApi<V,R,S> microserviceResource;
    private final String user;
    private final String passwordHash;

    public MicroserviceRestClient(MicroserviceResourceApi<V,R,S> microserviceResource, Class<R> factoryRootClass, String user, String passwordHash) {
        this.microserviceResource = microserviceResource;
        this.factoryRootClass = factoryRootClass;
        this.user=user;
        this.passwordHash=passwordHash;
    }

    public FactoryUpdateLog updateCurrentFactory(DataAndNewMetadata<R> update, String comment) {
        final UpdateCurrentFactoryRequest updateCurrentFactoryRequest = new UpdateCurrentFactoryRequest();
        updateCurrentFactoryRequest.comment=comment;
        updateCurrentFactoryRequest.factoryUpdate=update;
        return microserviceResource.updateCurrentFactory(new UserAwareRequest<>(user,passwordHash,updateCurrentFactoryRequest));
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataAndNewMetadata<R> update) {
        return microserviceResource.simulateUpdateCurrentFactory(new UserAwareRequest<>(user,passwordHash,update));
    }

    /**
     * @see DataStorage#getPrepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    @SuppressWarnings("unchecked")
    public DataAndNewMetadata<R> prepareNewFactory() {
        DataAndNewMetadata<R> currentFactory = microserviceResource.prepareNewFactory(new UserAwareRequest<>(user,passwordHash,null));
        return new DataAndNewMetadata<>(currentFactory.root.internal().prepareUsableCopy(),currentFactory.metadata);
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> getDiff(StoredDataMetadata historyEntry) {
        return microserviceResource.getDiff(new UserAwareRequest<>(user, passwordHash, historyEntry));
    }


    public R getHistoryFactory(String id) {
        R historyFactory = microserviceResource.getHistoryFactory(new UserAwareRequest<>(user, passwordHash, id)).value;
        return historyFactory.internal().prepareUsableCopy();
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return microserviceResource.getHistoryFactoryList(new UserAwareRequest<>(user, passwordHash, null));
    }

    @SuppressWarnings("unchecked")
    public ResponseWorkaround<V> query(V visitor) {
        return new ResponseWorkaround(microserviceResource.query(new UserAwareRequest<>(user,passwordHash,visitor)));
    }

    public boolean checkUser() {
        CheckUserResponse response = microserviceResource.checkUser(new UserAwareRequest<>(user,passwordHash,null));
        return response.valid;
    }

    public Locale getLocale() {
        UserLocaleResponse response = microserviceResource.getUserLocale(new UserAwareRequest<>(user,passwordHash,null));
        return response.locale;
    }

    public FactoryUpdateLog revert(StoredDataMetadata historyFactory) {
        return microserviceResource.revert(new UserAwareRequest<>(user,passwordHash,historyFactory));
    }


}
