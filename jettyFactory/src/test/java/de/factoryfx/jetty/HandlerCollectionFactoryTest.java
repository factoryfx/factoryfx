package de.factoryfx.jetty;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.storage.DataAndStoredMetadata;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.DispatcherType;
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
    @BeforeClass
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

    public static class HandlerCollectionResourceFactory extends SimpleFactoryBase<HandlerCollectionResource,Void, HandlerCollectionRootFactory> {
        @Override
        public HandlerCollectionResource createImpl() {
            return new HandlerCollectionResource();
        }
    }

    public static class HandlerCollectionRootFactory extends SimpleFactoryBase<Server,Void, HandlerCollectionRootFactory>{
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server,JettyServerFactory<Void, HandlerCollectionRootFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }
    }

    public static class CustomHandlerFactory<V,R extends FactoryBase<?,V,R>> extends PolymorphicFactoryBase<Handler,V,R> {
        @Override
        public Handler createImpl() {
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
        FactoryTreeBuilder<Void, Server, HandlerCollectionRootFactory, Void> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addFactory(HandlerCollectionRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void, HandlerCollectionRootFactory>())
                    .withHost("localhost").widthPort(8080).build();
        });


        Microservice<Void, Server, HandlerCollectionRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
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
        FactoryTreeBuilder<Void, Server, HandlerCollectionRootFactory, Void> builder = new FactoryTreeBuilder<>(HandlerCollectionRootFactory.class);
        builder.addFactory(HandlerCollectionRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void, HandlerCollectionRootFactory>())
                    .withResource(ctx.get(HandlerCollectionResourceFactory.class))
                    .withHost("localhost").widthPort(8080).build();
        });
        builder.addFactory(HandlerCollectionResourceFactory.class, Scope.SINGLETON);


        Microservice<Void, Server, HandlerCollectionRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/HandlerCollectionResource")).build();
            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.assertEquals(200, response.statusCode());
            }

            DataUpdate<HandlerCollectionRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().handler.get().handlers.clear();
            microservice.updateCurrentFactory(update);

            {

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.assertEquals(404, response.statusCode());

            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }
}