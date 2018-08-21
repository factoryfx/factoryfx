package de.factoryfx.microservice.rest.client;

import java.util.*;

import com.google.common.base.Supplier;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.DataStorage;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import de.factoryfx.factory.log.FactoryUpdateLog;

import de.factoryfx.microservice.common.*;

import javax.ws.rs.InternalServerErrorException;


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
    private final FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup;

    public MicroserviceRestClient(MicroserviceResourceApi<V,R,S> microserviceResource, Class<R> factoryRootClass, String user, String passwordHash, FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup) {
        this.microserviceResource = microserviceResource;
        this.factoryRootClass = factoryRootClass;
        this.user=user;
        this.passwordHash=passwordHash;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
    }

    public FactoryUpdateLog updateCurrentFactory(DataAndNewMetadata<R> update, String comment) {
        try {
            final UpdateCurrentFactoryRequest<R> updateCurrentFactoryRequest = new UpdateCurrentFactoryRequest<>();
            updateCurrentFactoryRequest.comment = comment;
            updateCurrentFactoryRequest.factoryUpdate = update;
            return microserviceResource.updateCurrentFactory(new UserAwareRequest<>(user, passwordHash, updateCurrentFactoryRequest));
        } catch (InternalServerErrorException e) {
            String respString = e.getResponse().readEntity(String.class);
            return new FactoryUpdateLog(respString);
        }
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataAndNewMetadata<R> update) {
        return executeWidthServerExceptionReporting(()->microserviceResource.simulateUpdateCurrentFactory(new UserAwareRequest<>(user,passwordHash,update)));
    }

    /**
     * @see DataStorage#prepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    @SuppressWarnings("unchecked")
    public DataAndNewMetadata<R> prepareNewFactory() {
        DataAndNewMetadata<R> currentFactory = executeWidthServerExceptionReporting(()->microserviceResource.prepareNewFactory(new UserAwareRequest<>(user,passwordHash,null)));
        R root = currentFactory.root.internal().addBackReferences();
        if (factoryTreeBuilderBasedAttributeSetup!=null){
            factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(root);
        }
        return new DataAndNewMetadata<>(root,currentFactory.metadata);
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> getDiff(StoredDataMetadata<S> historyEntry) {
        return executeWidthServerExceptionReporting(()->microserviceResource.getDiff(new UserAwareRequest<>(user, passwordHash, historyEntry)));
    }


    public R getHistoryFactory(String id) {
        R historyFactory = executeWidthServerExceptionReporting(()->microserviceResource.getHistoryFactory(new UserAwareRequest<>(user, passwordHash, id))).value;
        return historyFactory.internal().addBackReferences();
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return executeWidthServerExceptionReporting(()->microserviceResource.getHistoryFactoryList(new UserAwareRequest<>(user, passwordHash, null)));
    }

    @SuppressWarnings("unchecked")
    public ResponseWorkaround<V> query(V visitor) {
        return new ResponseWorkaround(executeWidthServerExceptionReporting(()->microserviceResource.query(new UserAwareRequest<>(user,passwordHash,visitor))));
    }

    public boolean checkUser() {
        CheckUserResponse response = executeWidthServerExceptionReporting(()->microserviceResource.checkUser(new UserAwareRequest<>(user,passwordHash,null)));
        return response.valid;
    }

    public Locale getLocale() {
        UserLocaleResponse response = executeWidthServerExceptionReporting(()->microserviceResource.getUserLocale(new UserAwareRequest<>(user,passwordHash,null)));
        return response.locale;
    }

    public FactoryUpdateLog revert(StoredDataMetadata<S> historyFactory) {
        return executeWidthServerExceptionReporting(()->microserviceResource.revert(new UserAwareRequest<>(user,passwordHash,historyFactory)));
    }

    /**execute a jersey proxy client action and get server error  */
    private <T> T executeWidthServerExceptionReporting(Supplier<T> action){
        try {
            return action.get();
        } catch (InternalServerErrorException e) {
            String respString = e.getResponse().readEntity(String.class);
            throw new RuntimeException("Server exception:\n----------------"+respString+"\n----------------",e);
        }
    }


}
