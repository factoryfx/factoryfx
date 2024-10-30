package io.github.factoryfx.jetty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.server.Microservice;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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
        public final FactoryAttribute<Server,JettyServerFactory<HandlerCollectionRootFactory>> server = new FactoryAttribute<>();

        @Override
        protected Server createImpl() {
            return server.instance();
        }
    }

    public static class CustomHandlerFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
        @Override
        protected Handler createImpl() {
            return new Handler.Abstract() {
                @Override
                public boolean handle(Request request, org.eclipse.jetty.server.Response response, Callback callback) {
                    return true;
                }
            };
        }
    }


    @Test
    public void test_add_handler_no_exception() {
        FactoryTreeBuilder<Server, HandlerCollectionRootFactory> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addBuilder(ctx->
            new SimpleJettyServerBuilder<HandlerCollectionRootFactory>()
                        .withHost("localhost").withPort(8087)
        );


        Microservice<Server, HandlerCollectionRootFactory> microservice = builder.microservice().build();
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
        FactoryTreeBuilder<Server, HandlerCollectionRootFactory> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addBuilder(ctx->new SimpleJettyServerBuilder<HandlerCollectionRootFactory>()
                .withHost("localhost").withPort(8087).withResource(ctx.get(HandlerCollectionResourceFactory.class))
        );


        builder.addFactory(HandlerCollectionResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, HandlerCollectionRootFactory> microservice = builder.microservice().build();
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