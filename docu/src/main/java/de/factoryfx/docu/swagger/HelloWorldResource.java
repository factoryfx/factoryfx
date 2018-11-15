package de.factoryfx.docu.swagger;

//import io.swagger.annotations.Api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Api
@Path("/HelloWorld")
public class HelloWorldResource {


    public HelloWorldResource(){
    }

    @GET
    public String get() {
        return "Hello World";
    }

    @GET
    @Path("/swagger.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response swagger() {
        Swagger swagger = new Swagger();
        Reader reader = new Reader(swagger, new DefaultReaderConfig());
        swagger = reader.read(Set.of(getClass()));

        try {
            return Response.ok(Json.pretty().writeValueAsString(swagger)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
