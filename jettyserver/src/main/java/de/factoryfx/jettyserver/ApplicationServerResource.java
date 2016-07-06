package de.factoryfx.jettyserver;

import java.util.Collection;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.factory.datastorage.ApplicationFactoryMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.merge.MergeDiff;
import de.factoryfx.server.ApplicationServer;

@Path("/") /** path defined in {@link de.scoopsoftware.xtc.ticketproxy.configuration.ConfigurationServer}*/
public class ApplicationServerResource<V, T extends FactoryBase<? extends LiveObject<V>, T>> implements ApplicationServer<V,T> {

    private final ApplicationServer<V,T> applicationServer;

    public ApplicationServerResource(ApplicationServer<V,T> applicationServer) {
        this.applicationServer = applicationServer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    @Override
    public MergeDiff updateCurrentFactory(ApplicationFactoryMetadata<T> updateFactory,Locale locale) {
        return  applicationServer.updateCurrentFactory(updateFactory,locale);
    }

    @Override
    public MergeDiff simulateUpdateCurrentFactory(ApplicationFactoryMetadata<T> updateFactory, Locale locale) {
        return applicationServer.simulateUpdateCurrentFactory(updateFactory,locale);
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


    @Override
    public V query(V visitor) {
        return applicationServer.query(visitor);
    }

}
