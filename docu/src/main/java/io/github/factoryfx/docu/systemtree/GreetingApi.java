package io.github.factoryfx.docu.systemtree;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * the shared contract between the two services: implemented by the greeting service,
 * called from the relay service through a typed jersey proxy.
 * both services depend on the jar containing this interface (the same pattern the
 * factoryfx admin channel uses with MicroserviceResourceApi).
 */
@Path("greeting")
public interface GreetingApi {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String greeting();
}
