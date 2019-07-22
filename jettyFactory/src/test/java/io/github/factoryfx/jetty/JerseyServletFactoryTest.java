package io.github.factoryfx.jetty;

import com.google.common.base.Throwables;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class JerseyServletFactoryTest {

    public static class JerseyServletTestErrorResourceFactory extends SimpleFactoryBase<JerseyServletTestErrorResource, JettyTestServerFactory> {
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

    public static class JettyTestServerFactory extends JettyServerFactory<JettyTestServerFactory>{

    }

    public class TestExceptionMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable exception) {
            return Response.status(542).build();
        }
    }

    @Test
    public void test_exception_mapper() {
        FactoryTreeBuilder<Server, JettyTestServerFactory, Void> builder = new FactoryTreeBuilder<>(JettyTestServerFactory.class, ctx->{
            return new JettyServerBuilder<JettyTestServerFactory>()
                    .withResource(ctx.get(JerseyServletTestErrorResourceFactory.class))
                    .withHost("localhost").withPort(8087).withExceptionMapper(new TestExceptionMapper()).buildTo(new JettyTestServerFactory());
        });
        builder.addFactory(JerseyServletTestErrorResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, JettyTestServerFactory, Void> microservice = builder.microservice().build();
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