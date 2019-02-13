package de.factoryfx.docu.customconfig;

//import io.swagger.annotations.Api;

import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.server.Microservice;
import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Path("/CustomConfiguration")
public class CustomConfigurationResource {

    private final Microservice<Void, ?, ServerFactory, ?> microservice;

    public CustomConfigurationResource(Microservice<Void, ?, ServerFactory, ?> microservice) {
        this.microservice=microservice;
    }

    public static class CustomConfigurationRequest{
        public int port;
    }

    @SuppressWarnings("unchecked")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response config(CustomConfigurationRequest request) {
        DataAndStoredMetadata update = microservice.prepareNewFactory();
        ((ServerFactory)update.root).server.get().connectors.get(0).port.set(request.port);
        microservice.updateCurrentFactory("CustomConfigurationResource","port change",update);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() { //just for testing not need for configuration
        return Response.ok().build();
    }

}