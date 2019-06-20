package io.github.factoryfx.jetty;

import ch.qos.logback.classic.Level;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.server.Microservice;
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

    public static class UpdateableTestResourceFactory extends SimpleFactoryBase<UpdateableTestResource, JettyServerRootFactory> {
        public final StringAttribute response = new StringAttribute().nullable();
        @Override
        protected UpdateableTestResource createImpl() {
            return new UpdateableTestResource(response.get());
        }
    }

    public static class JettyServerRootFactory extends JettyServerFactory<JettyServerRootFactory>{
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_jersey_after_update() throws IOException, InterruptedException {
        FactoryTreeBuilder<Server, JettyServerRootFactory, Void> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class, ctx->{
            return new JettyServerBuilder<JettyServerRootFactory>()
                    .withHost("localhost").withPort(8080)
                    .withResource(ctx.get(UpdateableTestResourceFactory.class)).buildTo(new JettyServerRootFactory());
        });
        builder.addFactory(UpdateableTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            return resource;
        });

        Microservice<Server, JettyServerRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();
            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("123",response.body());
            }


            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.getResource(UpdateableTestResourceFactory.class).response.set("abc");
            microservice.updateCurrentFactory(update);

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("abc", response.body());
            }

        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_remove_resource() throws IOException, InterruptedException {
        FactoryTreeBuilder<Server, JettyServerRootFactory, Void> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class, ctx->{
            return new JettyServerBuilder<JettyServerRootFactory>()
                    .withHost("localhost").withPort(8080)
                    .withResource(ctx.get(UpdateableTestResourceFactory.class)).buildTo(new JettyServerRootFactory());

        });
        builder.addFactory(UpdateableTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            return resource;
        });

        Microservice<Server, JettyServerRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.clearResource(UpdateableTestResourceFactory.class);
            microservice.updateCurrentFactory(update);

            {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(404, response.statusCode());
            }
        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_add_resource() throws IOException, InterruptedException {
        FactoryTreeBuilder<Server, JettyServerRootFactory, Void> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class, ctx->{
            return new JettyServerBuilder<JettyServerRootFactory>()
                    .withHost("localhost").withPort(8080).buildTo(new JettyServerRootFactory());

        });


        Microservice<Server, JettyServerRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/UpdateableTestResource")).build();

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();

            UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
            resource.response.set("123");
            update.root.setResource(resource);

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
        FactoryTreeBuilder<Server, JettyServerRootFactory, Void> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class, ctx->{
            return new JettyServerBuilder<JettyServerRootFactory>()
                    .withHost("localhost").withPort(8080).buildTo(new JettyServerRootFactory());
        });


        Microservice<Server, JettyServerRootFactory, Void> microservice = builder.microservice().build();
        microservice.start();
        try {

            {
                DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();

                UpdateableTestResourceFactory resource = new UpdateableTestResourceFactory();
                resource.response.set("123");

                ServletContextHandlerFactory<JettyServerRootFactory> servletContextHandler = (ServletContextHandlerFactory<JettyServerRootFactory>) ( update.root.handler.get().handlers.get(1));
                JerseyServletFactory<JettyServerRootFactory> jerseyServletFactory = new JerseyServletFactory<>();
                jerseyServletFactory.restLogging.set(new Slf4LoggingFeatureFactory<>());
                jerseyServletFactory.resources.add(resource);
                ServletAndPathFactory<JettyServerRootFactory> jerseyServletFactoryPath = new ServletAndPathFactory<>();
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