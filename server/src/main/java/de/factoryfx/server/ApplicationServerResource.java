package de.factoryfx.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class ApplicationServerResource<T extends FactoryBase<? extends LiveObject, T>> {

    private final FactoryManager<T> factoryManager;
    private final FactoryStorage<T> factoryStorage;

    public ApplicationServerResource(FactoryManager factoryManager, FactoryStorage<T> factoryStorage) {
        this.factoryManager = factoryManager;
        this.factoryStorage = factoryStorage;
    }

    public static class UpdateFactoryRequest<T extends FactoryBase<? extends LiveObject, T>>{
        public ApplicationFactoriesMetadata<T> newFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    public Response updateCurrentFactory(UpdateFactoryRequest<T> updateFactoryRequest) {
        MergeDiff mergeDiff = factoryManager.update(factoryStorage.getHistoryFactory(updateFactoryRequest.newFactory.baseVersionId).root,updateFactoryRequest.newFactory.root);
        if (mergeDiff.hasNoConflicts()){
            factoryStorage.updateCurrentFactory(updateFactoryRequest.newFactory.root);
        } else {
            //TODO real merge conflict handling
            return Response.serverError().entity("merge conflict").build();
        }

        return  Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("currentFactory")
    public Response getCurrentFactory() {
        return Response.ok(factoryStorage.getCurrentFactory()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    public Response getHistoryFactory(String id) {
        return Response.ok(factoryStorage.getHistoryFactory(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    public Response getHistoryFactoryList() {
        return Response.ok(factoryStorage.getHistoryFactoryList()).build();
    }




}
