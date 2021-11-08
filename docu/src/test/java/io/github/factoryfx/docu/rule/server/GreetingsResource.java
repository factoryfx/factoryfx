package io.github.factoryfx.docu.rule.server;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("greetings")
public class GreetingsResource {

    private final BackendClient backendClient;

    public GreetingsResource(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @GET
    @Path("greet")
    @Produces(MediaType.TEXT_PLAIN)
    public String greet(){

        return backendClient.hello() + " world";
    }
}
