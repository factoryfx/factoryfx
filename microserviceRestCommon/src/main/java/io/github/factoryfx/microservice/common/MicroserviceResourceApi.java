package io.github.factoryfx.microservice.common;

import java.util.Collection;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


/**
 * REST resource API for managing a microservice. start,stop,update,..
 *
 */
@Path("microservice")
public interface MicroserviceResourceApi<R extends FactoryBase<?,R>>  {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    FactoryUpdateLog<R> updateCurrentFactory(UserAwareRequest<DataUpdate<R>> update);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("revert")
    FactoryUpdateLog<R> revert(UserAwareRequest<StoredDataMetadata> historyFactory) ;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulateUpdateCurrentFactory")
    MergeDiffInfo<R> simulateUpdateCurrentFactory(UserAwareRequest<DataUpdate<R>> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    MergeDiffInfo<R> getDiff(UserAwareRequest<StoredDataMetadata> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("prepareNewFactory")
    DataUpdate<R> prepareNewFactory(VoidUserAwareRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    ResponseWorkaround<R> getHistoryFactory(UserAwareRequest<String> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    Collection<StoredDataMetadata> getHistoryFactoryList(VoidUserAwareRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("checkUser")
    CheckUserResponse checkUser(VoidUserAwareRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userLocale")
    UserLocaleResponse getUserLocale(VoidUserAwareRequest request);
}
