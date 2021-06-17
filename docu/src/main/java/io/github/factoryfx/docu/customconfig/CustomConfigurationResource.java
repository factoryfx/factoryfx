package io.github.factoryfx.docu.customconfig;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryUpdate;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

@Path("/CustomConfiguration")
public class CustomConfigurationResource {

    private final Microservice<?, JettyServerRootFactory> microservice;

    public CustomConfigurationResource(Microservice<?, JettyServerRootFactory> microservice) {
        this.microservice=microservice;
    }

    public static class CustomConfigurationRequest{
        public int port;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response config(CustomConfigurationRequest request) {
        microservice.update((root, idToFactory) -> root.connectors.get(0).port.set(request.port));
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() { //just for testing not need for configuration
        return Response.ok().build();
    }

}
