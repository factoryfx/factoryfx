package io.github.factoryfx.docu.restserver;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

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
