package io.github.factoryfx.docu.monitoring;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/*
    minimal example for a http server
 */
@Path("/")
public class SimpleResource {

    @GET
    public Response get(){
        return Response.ok().build();
    }
}
