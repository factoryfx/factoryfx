package io.github.factoryfx.example.server.factoryupdatelogleak;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/other")
public class SomeOtherResource {

    private final NestedObject nested;

    public SomeOtherResource(NestedObject nested) {
        this.nested = nested;
    }

    @GET
    @Path("nested/boolean")
    public Boolean getBooleanValue() {
        return this.nested.isaBoolean();
    }
}
