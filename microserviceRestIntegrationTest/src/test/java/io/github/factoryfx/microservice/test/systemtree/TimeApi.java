package io.github.factoryfx.microservice.test.systemtree;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * toy cross-service contract: implemented by service A, called from service B through a typed Jersey proxy
 * (same interface-based pattern as MicroserviceResourceApi)
 */
@Path("timeApi")
public interface TimeApi {

    @GET
    @Path("greeting")
    @Produces(MediaType.TEXT_PLAIN)
    String greeting();
}
