package de.factoryfx.server.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.server.ApplicationServer;

@Path("/") /** path defined in {@link de.factoryfx.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class ApplicationServerResource<L,V,T extends FactoryBase<L,V>> {

    private final ApplicationServer<L,V,T> applicationServer;

    public ApplicationServerResource(ApplicationServer<L,V,T> applicationServer) {
        this.applicationServer = applicationServer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    public MergeDiff updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return  applicationServer.updateCurrentFactory(update);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("currentFactory")
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        return applicationServer.getCurrentFactory();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    public T getHistoryFactory(String id) {
        return applicationServer.getHistoryFactory(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return applicationServer.getHistoryFactoryList();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("start")
    public void start() {
        applicationServer.start();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("stop")
    public void stop() {
        applicationServer.stop();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    public V query(V visitor) {
        return applicationServer.query(visitor);
    }

}
