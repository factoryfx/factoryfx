package io.github.factoryfx.docu.starter;

import java.lang.String;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Example jersey REST resource */
@Path("/")
public class ExampleResource {
  @GET
  public String get() {
    return "Hello World";
  }
}
