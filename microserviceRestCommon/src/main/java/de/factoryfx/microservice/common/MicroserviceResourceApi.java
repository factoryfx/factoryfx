package de.factoryfx.microservice.common;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.microservice.common.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;


/**
 *https://stackoverflow.com/questions/17000193/can-we-have-more-than-one-path-annotation-for-same-rest-method
 *3 Paths for compatibility
 *microservice is new one
 */
@Path("microservice")
public interface MicroserviceResourceApi<V,R extends FactoryBase<?,V,R>,S>  {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    FactoryUpdateLog updateCurrentFactory(UserAwareRequest<UpdateCurrentFactoryRequest> update);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("revert")
    FactoryUpdateLog revert(UserAwareRequest<StoredDataMetadata> historyFactory) ;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulateUpdateCurrentFactory")
    MergeDiffInfo simulateUpdateCurrentFactory(UserAwareRequest<DataAndNewMetadata> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    MergeDiffInfo getDiff(UserAwareRequest<StoredDataMetadata> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("prepareNewFactory")
    DataAndNewMetadata prepareNewFactory(UserAwareRequest<Void> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    ResponseWorkaround<R> getHistoryFactory(UserAwareRequest<String> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    Collection<StoredDataMetadata<S>> getHistoryFactoryList(UserAwareRequest<Void> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    ResponseWorkaround<V> query(UserAwareRequest<V> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("queryReadOnly")
    ResponseWorkaround<V> queryReadOnly(UserAwareRequest<Void> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("checkUser")
    CheckUserResponse checkUser(UserAwareRequest<Void> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userKey")
    KeyResponse getUserKey(UserAwareRequest<Void> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userLocale")
    UserLocaleResponse getUserLocale(UserAwareRequest<Void> request);
}
