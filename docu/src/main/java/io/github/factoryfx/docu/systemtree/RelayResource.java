package io.github.factoryfx.docu.systemtree;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** endpoint of the relay service: combines its local config value with a call through the typed GreetingApi proxy */
@Path("relay")
public class RelayResource {

    private final String localGreeting;
    private final GreetingApi remoteGreeting;

    public RelayResource(String localGreeting, GreetingApi remoteGreeting) {
        this.localGreeting = localGreeting;
        this.remoteGreeting = remoteGreeting;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String combined() {
        return localGreeting + "|" + remoteGreeting.greeting();
    }
}
