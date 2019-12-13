package io.github.factoryfx.microservice.rest.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Supplier;

import javax.ws.rs.InternalServerErrorException;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.microservice.common.CheckUserResponse;
import io.github.factoryfx.microservice.common.MicroserviceResourceApi;
import io.github.factoryfx.microservice.common.UserAwareRequest;
import io.github.factoryfx.microservice.common.UserLocaleResponse;
import io.github.factoryfx.microservice.common.VoidUserAwareRequest;
import io.github.factoryfx.server.Microservice;


/**
 *
 * @param <R> Root factory
 */
public class MicroserviceRestClient<R extends FactoryBase<?,R>> {

    private final MicroserviceResourceApi<R> microserviceResourceApi;
    private final String user;
    private final String passwordHash;
    private final FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup;

    public MicroserviceRestClient(MicroserviceResourceApi<R> microserviceResourceApi, String user, String passwordHash, FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup) {
        this.microserviceResourceApi = microserviceResourceApi;
        this.user=user;
        this.passwordHash=passwordHash;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
    }

    public FactoryUpdateLog<R> updateCurrentFactory(DataUpdate<R> update, String comment) {
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
            return new FactoryUpdateLog<>(respString);
        }
    }

    public MergeDiffInfo<R> simulateUpdateCurrentFactory(DataUpdate<R> update) {
        return executeWithServerExceptionReporting(()-> microserviceResourceApi.simulateUpdateCurrentFactory(new UserAwareRequest<>(user,passwordHash,update)));
    }

    /**
     * @see Microservice#prepareNewFactory()
     *
     * @return new factory for editing, server assign new id for the update
     */
    public DataUpdate<R> prepareNewFactory() {
        DataUpdate<R> update = executeWithServerExceptionReporting(()-> microserviceResourceApi.prepareNewFactory(new VoidUserAwareRequest(user,passwordHash)));
        update.root.internal().finalise();
        if (factoryTreeBuilderBasedAttributeSetup!=null){
            factoryTreeBuilderBasedAttributeSetup.applyToRootFactoryDeep(update.root);
        }
        return update;
    }

    public MergeDiffInfo<R> getDiff(StoredDataMetadata historyEntry) {
        return executeWithServerExceptionReporting(()-> microserviceResourceApi.getDiff(new UserAwareRequest<>(user, passwordHash, historyEntry)));
    }


    public R getHistoryFactory(String id) {
        R historyFactory = executeWithServerExceptionReporting(()-> microserviceResourceApi.getHistoryFactory(new UserAwareRequest<>(user, passwordHash, id))).value;
        historyFactory.internal().finalise();
        return historyFactory;
    }

    public Collection<StoredDataMetadata> getHistoryFactoryList() {
        return executeWithServerExceptionReporting(()-> microserviceResourceApi.getHistoryFactoryList(new VoidUserAwareRequest(user, passwordHash)));
    }

    public boolean checkUser() {
        CheckUserResponse response = executeWithServerExceptionReporting(()-> microserviceResourceApi.checkUser(new VoidUserAwareRequest(user,passwordHash)));
        return response.valid;
    }

    public Locale getLocale() {
        UserLocaleResponse response = executeWithServerExceptionReporting(()-> microserviceResourceApi.getUserLocale(new VoidUserAwareRequest(user,passwordHash)));
        return response.locale;
    }

    public FactoryUpdateLog<R> revert(StoredDataMetadata historyFactory) {
        return executeWithServerExceptionReporting(()-> microserviceResourceApi.revert(new UserAwareRequest<>(user,passwordHash,historyFactory)));
    }

    /**execute a jersey proxy client action and get server error  */
    private <T> T executeWithServerExceptionReporting(Supplier<T> action){
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
