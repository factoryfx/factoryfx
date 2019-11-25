package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.DispatcherType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class GzipHandlerFactoryTest {

    @Path("/test")
    public static class JerseyServletTestGZipResource{
        @GET()
        @Produces(MediaType.TEXT_PLAIN)
        public String get(){
            return "123";
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
            Assertions.assertEquals("123", response.body());
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
            Assertions.assertNotEquals("123",response.body());
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