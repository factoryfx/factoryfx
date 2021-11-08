package io.github.factoryfx.jetty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class JerseyServletFactoryTest {

    public static class JerseyServletTestErrorResourceFactory extends SimpleFactoryBase<JerseyServletTestErrorResource, JettyServerRootFactory> {
        @Override
        protected JerseyServletTestErrorResource createImpl() {
            return new JerseyServletTestErrorResource();
        }
    }

    @Path("/test")
    public static class JerseyServletTestErrorResource{

        @GET()
        public Response get(){
            throw new IllegalArgumentException();
        }
    }

    public static class TestExceptionMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable exception) {
            return Response.status(542).build();
        }
    }

    @Test
    public void test_exception_mapper() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                    withHost("localhost").withPort(8087).withExceptionMapper(AttributelessFactory.create(TestExceptionMapper.class)).withResource(ctx.get(JerseyServletTestErrorResourceFactory.class))
                );


        builder.addFactory(JerseyServletTestErrorResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/test")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(542, response.statusCode());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }
}