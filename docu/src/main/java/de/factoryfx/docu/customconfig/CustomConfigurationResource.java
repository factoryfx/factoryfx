package de.factoryfx.docu.customconfig;

//import io.swagger.annotations.Api;

import de.factoryfx.data.storage.DataUpdate;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response config(CustomConfigurationRequest request) {
        DataUpdate<ServerFactory> update = microservice.prepareNewFactory("CustomConfigurationResource","port change");
        update.root.server.get().connectors.get(0).port.set(request.port);
        microservice.updateCurrentFactory(update);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() { //just for testing not need for configuration
        return Response.ok().build();
    }

}
