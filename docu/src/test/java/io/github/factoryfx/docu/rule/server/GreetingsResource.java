package io.github.factoryfx.docu.rule.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
