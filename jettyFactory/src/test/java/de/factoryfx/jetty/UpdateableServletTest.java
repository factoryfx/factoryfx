package de.factoryfx.jetty;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateableServletTest {

    @BeforeAll
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @Path("/UpdateableTestResource")
    public static class UpdateableTestResource{
        private final String response;

        public UpdateableTestResource(String response) {
            this.response = response;
        }

        @GET()
        public Response get(){
            return Response.ok(this.response).build();
        }
    }

    public static class UpdateableTestResourceFactory extends SimpleFactoryBase<UpdateableTestResource,Void, UpdateableWebserverRootFactory> {
        public final StringAttribute response = new StringAttribute().nullable();
        @Override
        public UpdateableTestResource createImpl() {
            return new UpdateableTestResource(response.get());
        }
    }

    public static class UpdateableWebserverRootFactory extends SimpleFactoryBase<Server,Void, UpdateableWebserverRootFactory>{
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server,JettyServerFactory<Void, UpdateableWebserverRootFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_jersey_after_update() throws IOException, InterruptedException {
        FactoryTreeBuilder<Void, Server, UpdateableWebserverRootFactory, Void> builder = new FactoryTreeBuilder<>(UpdateableWebserverRootFactory.class);
        builder.addFactory(UpdateableWebserverRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,UpdateableWebserverRootFactory>())
                    .withHost("localhost").widthPort(8080)
                    .withResource(ctx.get(UpdateableTestResourceFactory.class)).build();
        });
        builder.addFactory(UpdateableTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            return resource;
        });

        Microservice<Void, Server, UpdateableWebserverRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
        try {

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();
            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("123",response.body());
            }


            DataUpdate<UpdateableWebserverRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().getResource(UpdateableTestResourceFactory.class).response.set("abc");
            microservice.updateCurrentFactory(update);

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("abc", response.body());
            }

        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_remove_resource() throws IOException, InterruptedException {
        FactoryTreeBuilder<Void, Server, UpdateableWebserverRootFactory, Void> builder = new FactoryTreeBuilder<>(UpdateableWebserverRootFactory.class);
        builder.addFactory(UpdateableWebserverRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,UpdateableWebserverRootFactory>())
                    .withHost("localhost").widthPort(8080)
                    .withResource(ctx.get(UpdateableTestResourceFactory.class)).build();

        });
        builder.addFactory(UpdateableTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            return resource;
        });

        Microservice<Void, Server, UpdateableWebserverRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();

            DataUpdate<UpdateableWebserverRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().clearResource(UpdateableTestResourceFactory.class);
            microservice.updateCurrentFactory(update);

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(404, response.statusCode());
            }
        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_add_resource() throws IOException, InterruptedException {
        FactoryTreeBuilder<Void, Server, UpdateableWebserverRootFactory, Void> builder = new FactoryTreeBuilder<>(UpdateableWebserverRootFactory.class);
        builder.addFactory(UpdateableWebserverRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,UpdateableWebserverRootFactory>())
                    .withHost("localhost").widthPort(8080).build();

        });


        Microservice<Void, Server, UpdateableWebserverRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();

            DataUpdate<UpdateableWebserverRootFactory> update = microservice.prepareNewFactory();

            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            update.root.server.get().setResource(resource);

            microservice.updateCurrentFactory(update);

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("123", response.body());
            }
        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_add_jerseyServlet() throws IOException, InterruptedException {
        FactoryTreeBuilder<Void, Server, UpdateableWebserverRootFactory, Void> builder = new FactoryTreeBuilder<>(UpdateableWebserverRootFactory.class);
        builder.addFactory(UpdateableWebserverRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,UpdateableWebserverRootFactory>())
                    .withHost("localhost").widthPort(8080).build();
        });


        Microservice<Void, Server, UpdateableWebserverRootFactory, Void> microservice = builder.microservice().withInMemoryStorage().build();
        microservice.start();
        try {

            {
                DataUpdate<UpdateableWebserverRootFactory> update = microservice.prepareNewFactory();

                UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
                resource.response.set("123");

                ServletContextHandlerFactory<Void, UpdateableWebserverRootFactory> servletContextHandler = (ServletContextHandlerFactory<Void, UpdateableWebserverRootFactory>) ((GzipHandlerFactory<Void, UpdateableWebserverRootFactory>) update.root.server.get().handler.get().handlers.get(0)).handler.get();
                JerseyServletFactory<Void, UpdateableWebserverRootFactory> jerseyServletFactory = new JerseyServletFactory<>();
                jerseyServletFactory.restLogging.set(new Slf4LoggingFeatureFactory<>());
                jerseyServletFactory.resources.add(resource);
                ServletAndPathFactory<Void, UpdateableWebserverRootFactory> jerseyServletFactoryPath = new ServletAndPathFactory<>();
                jerseyServletFactoryPath.servlet.set(jerseyServletFactory);
                jerseyServletFactoryPath.pathSpec.set("/new/*");
                servletContextHandler.updatableRootServlet.get().servletAndPaths.add(jerseyServletFactoryPath);

                microservice.updateCurrentFactory(update);
            }

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/new/UpdateableTestResource")).build();

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("123", response.body());
            }
        } finally {
            microservice.stop();
        }
    }

}