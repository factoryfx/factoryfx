package io.github.factoryfx.jetty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import jakarta.servlet.DispatcherType;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

class GzipHandlerFactoryTest {
    private static final String RESPONSE_STRING = "123456789123456789123456789123456789123456789";

    @Path("/test")
    public static class JerseyServletTestGZipResource{
        @GET()
        @Produces(MediaType.TEXT_PLAIN)
        public String get(){
            return RESPONSE_STRING;
        }
    }

    public static class JerseyServletTestGZipResourceFactory extends SimpleFactoryBase<JerseyServletTestGZipResource, JettyServerRootFactory> {
        @Override
        protected JerseyServletTestGZipResource createImpl() {
            return new JerseyServletTestGZipResource();
        }
    }

    @Test
    public void test_client_disabled_gzip() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(ctx.get(JerseyServletTestGZipResourceFactory.class))
        );
        builder.addFactory(JerseyServletTestGZipResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/test")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(RESPONSE_STRING, response.body());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }


    }

    @Test
    public void test_client_enabled_gzip() { // header Accept-Encoding: gzip
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(ctx.get(JerseyServletTestGZipResourceFactory.class))
        );
        builder.addFactory(JerseyServletTestGZipResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/test")).header("Accept-Encoding","gzip").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals("gzip",response.headers().firstValue("Content-Encoding").get());
            Assertions.assertNotEquals(RESPONSE_STRING, response.body());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }

    }


    @Test
    public void test_visitor(){

        GzipHandlerFactory gzipHandlerFactory = new GzipHandlerFactory();
        gzipHandlerFactory.internal().visitAttributesMetadata(metadata -> {
            if (metadata.attributeVariableName.equals("dispatcherTypes")){
                Assertions.assertEquals(DispatcherType.class,metadata.enumClass);
            }

        });

    }
}