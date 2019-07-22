package io.github.factoryfx.jetty;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HandlerCollectionFactoryTest {
    @BeforeAll
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @Path("/HandlerCollectionResource")
    public static class HandlerCollectionResource{

        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    public static class HandlerCollectionResourceFactory extends SimpleFactoryBase<HandlerCollectionResource, HandlerCollectionRootFactory> {
        @Override
        protected HandlerCollectionResource createImpl() {
            return new HandlerCollectionResource();
        }
    }
    public static class HandlerCollectionRootFactory extends SimpleFactoryBase<Server, HandlerCollectionRootFactory>{
        public final FactoryAttribute<HandlerCollectionRootFactory,Server,JettyServerFactory<HandlerCollectionRootFactory>> server = new FactoryAttribute<>();

        @Override
        protected Server createImpl() {
            return server.instance();
        }
    }

    public static class CustomHandlerFactory<R extends FactoryBase<?,R>> extends PolymorphicFactoryBase<Handler,R> {
        @Override
        protected Handler createImpl() {
            return new AbstractHandler() {
                @Override
                public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

                }
            };
        }
    }


    @SuppressWarnings("unchecked")
    @Test
    public void test_add_handler_no_exception() {
        FactoryTreeBuilder<Server, HandlerCollectionRootFactory, Void> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<HandlerCollectionRootFactory>()
                    .withHost("localhost").withPort(8087).build();
        });


        Microservice<Server, HandlerCollectionRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {

            DataUpdate<HandlerCollectionRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().handler.get().handlers.add(new CustomHandlerFactory<>());
            microservice.updateCurrentFactory(update);

        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_remove_handler() {
        FactoryTreeBuilder<Server, HandlerCollectionRootFactory, Void> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<HandlerCollectionRootFactory>()
                    .withResource(ctx.get(HandlerCollectionResourceFactory.class))
                    .withHost("localhost").withPort(8087).build();
        });
        builder.addFactory(HandlerCollectionResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, HandlerCollectionRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/HandlerCollectionResource")).build();
            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200, response.statusCode());
            }

            DataUpdate<HandlerCollectionRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().handler.get().handlers.clear();
            microservice.updateCurrentFactory(update);

            {

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(404, response.statusCode());

            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }
}