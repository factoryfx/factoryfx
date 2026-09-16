package io.github.factoryfx.microservice.test.systemtree;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** consumer endpoint on service B: combines B's local config value with a call through the typed TimeApi proxy to A */
@Path("relay")
public class GreetingRelayResource {

    private final String localGreeting;
    private final TimeApi remoteTime;

    public GreetingRelayResource(String localGreeting, TimeApi remoteTime) {
        this.localGreeting = localGreeting;
        this.remoteTime = remoteTime;
    }

    @GET
    @Path("combined")
    @Produces(MediaType.TEXT_PLAIN)
    public String combined() {
        return localGreeting + "|" + remoteTime.greeting();
    }
}
