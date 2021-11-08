package io.github.factoryfx.docu.initializr;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Example jersey REST resource */
@Path("/")
public class ExampleResource {
  @GET
  public String get() {
    return "Hello World";
  }
}
