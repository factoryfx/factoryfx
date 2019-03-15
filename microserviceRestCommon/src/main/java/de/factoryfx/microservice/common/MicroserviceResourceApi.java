package de.factoryfx.microservice.common;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataUpdate;
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
 * REST resource API for managing a microservice. start,stop,update,..
 *
 */
@Path("microservice")
public interface MicroserviceResourceApi<R extends FactoryBase<?,R>,S>  {


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    FactoryUpdateLog<R> updateCurrentFactory(UserAwareRequest<DataUpdate<R>> update);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("revert")
    FactoryUpdateLog<R> revert(UserAwareRequest<StoredDataMetadata<S>> historyFactory) ;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulateUpdateCurrentFactory")
    MergeDiffInfo<R> simulateUpdateCurrentFactory(UserAwareRequest<DataUpdate<R>> request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    MergeDiffInfo<R> getDiff(UserAwareRequest<StoredDataMetadata<S>> request);

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
    Collection<StoredDataMetadata<S>> getHistoryFactoryList(VoidUserAwareRequest request);

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
