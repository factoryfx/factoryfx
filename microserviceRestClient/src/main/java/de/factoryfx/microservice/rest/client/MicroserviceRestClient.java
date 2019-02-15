package de.factoryfx.microservice.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;


import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.FactoryBase;
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
    private final MicroserviceResourceApi<V,R,S> microserviceResourceApi;
    private final String user;
    private final String passwordHash;
    private final FactoryTreeBuilderBasedAttributeSetup<V,?,R,S> factoryTreeBuilderBasedAttributeSetup;

    public MicroserviceRestClient(MicroserviceResourceApi<V,R,S> microserviceResourceApi, Class<R> factoryRootClass, String user, String passwordHash, FactoryTreeBuilderBasedAttributeSetup<V,?,R,S> factoryTreeBuilderBasedAttributeSetup) {
        this.microserviceResourceApi = microserviceResourceApi;
        this.factoryRootClass = factoryRootClass;
        this.user=user;
        this.passwordHash=passwordHash;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
    }

    public FactoryUpdateLog updateCurrentFactory(DataUpdate<R> update, String comment) {
        try {
            DataUpdate<R> updateMetadata = new DataUpdate<>(
                    update.root,
                    update.user,
                    comment,
                    update.baseVersionId
            );

            return microserviceResourceApi.updateCurrentFactory(new UserAwareRequest<>(user, passwordHash, updateMetadata));
        } catch (InternalServerErrorException e) {
            String respString = e.getResponse().readEntity(String.class);
            return new FactoryUpdateLog(respString);
        }
    }

    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> update) {
        return executeWidthServerExceptionReporting(()-> microserviceResourceApi.simulateUpdateCurrentFactory(new UserAwareRequest<>(user,passwordHash,update)));
    }

    /**
     * @see de.factoryfx.server.Microservice#prepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    public DataUpdate<R> prepareNewFactory() {
        DataUpdate<R> update = executeWidthServerExceptionReporting(()-> microserviceResourceApi.prepareNewFactory(new VoidUserAwareRequest(user,passwordHash)));
        update.root.internal().addBackReferences();
        if (factoryTreeBuilderBasedAttributeSetup!=null){
            factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(update.root);
        }
        return update;
    }

    public MergeDiffInfo<R> getDiff(StoredDataMetadata<S> historyEntry) {
        return executeWidthServerExceptionReporting(()-> microserviceResourceApi.getDiff(new UserAwareRequest<>(user, passwordHash, historyEntry)));
    }


    public R getHistoryFactory(String id) {
        R historyFactory = executeWidthServerExceptionReporting(()-> microserviceResourceApi.getHistoryFactory(new UserAwareRequest<>(user, passwordHash, id))).value;
        return historyFactory.internal().addBackReferences();
    }

    public Collection<StoredDataMetadata<S>> getHistoryFactoryList() {
        return executeWidthServerExceptionReporting(()-> microserviceResourceApi.getHistoryFactoryList(new VoidUserAwareRequest(user, passwordHash)));
    }

    @SuppressWarnings("unchecked")
    public ResponseWorkaround<V> query(V visitor) {
        return new ResponseWorkaround(executeWidthServerExceptionReporting(()-> microserviceResourceApi.query(new UserAwareRequest<>(user,passwordHash,visitor))));
    }

    public boolean checkUser() {
        CheckUserResponse response = executeWidthServerExceptionReporting(()-> microserviceResourceApi.checkUser(new VoidUserAwareRequest(user,passwordHash)));
        return response.valid;
    }

    public Locale getLocale() {
        UserLocaleResponse response = executeWidthServerExceptionReporting(()-> microserviceResourceApi.getUserLocale(new VoidUserAwareRequest(user,passwordHash)));
        return response.locale;
    }

    public FactoryUpdateLog revert(StoredDataMetadata<S> historyFactory) {
        return executeWidthServerExceptionReporting(()-> microserviceResourceApi.revert(new UserAwareRequest<>(user,passwordHash,historyFactory)));
    }

    /**execute a jersey proxy client action and get server error  */
    private <T> T executeWidthServerExceptionReporting(Supplier<T> action){
        try {
            return action.get();
        } catch (InternalServerErrorException e) {

            String respString= null;
            if (e.getResponse().getEntity() instanceof ByteArrayInputStream ){
                try {
                    respString = CharStreams.toString(new InputStreamReader(((ByteArrayInputStream)e.getResponse().getEntity()), Charsets.UTF_8));
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
            throw new RuntimeException("Server exception:\n----------------"+respString+"\n----------------",e);
        }
    }


}
