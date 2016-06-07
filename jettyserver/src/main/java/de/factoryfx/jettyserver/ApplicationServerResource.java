package de.factoryfx.jettyserver;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class ApplicationServerResource<T extends FactoryBase<? extends LiveObject, T>> implements ApplicationServer<T> {

    private final ApplicationServer<T> applicationServer;

    public ApplicationServerResource(ApplicationServer<T> applicationServer) {
        this.applicationServer = applicationServer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    @Override
    public MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> newFactory) {
        return  applicationServer.updateCurrentFactory(newFactory);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("currentFactory")
    @Override
    public ApplicationFactoryMetadata<T> getCurrentFactory() {
        return applicationServer.getCurrentFactory();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    @Override
    public ApplicationFactoryMetadata<T> getHistoryFactory(String id) {
        return applicationServer.getHistoryFactory(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    @Override
    public Collection<ApplicationFactoryMetadata<T>> getHistoryFactoryList() {
        return applicationServer.getHistoryFactoryList();
    }

    @Override
    public void start() {
        applicationServer.start();
    }

    @Override
    public void stop() {
        applicationServer.stop();
    }

}
