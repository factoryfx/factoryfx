package de.factoryfx.docu.monitoring;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
