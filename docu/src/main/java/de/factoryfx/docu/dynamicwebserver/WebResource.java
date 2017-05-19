package de.factoryfx.docu.dynamicwebserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/Resource")
public class WebResource {

    final String response;

    public WebResource(String response) {
        this.response = response;
    }

    @GET
    public String get() {
        return response;
    }
}
