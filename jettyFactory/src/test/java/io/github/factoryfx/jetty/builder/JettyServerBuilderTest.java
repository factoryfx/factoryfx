package io.github.factoryfx.jetty.builder;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.jetty.JerseyServletFactoryTest;
import io.github.factoryfx.server.Microservice;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JettyServerBuilderTest {

    public static class DummyResourceFactory extends SimpleFactoryBase<Void, JettyServerRootFactory> {

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @BeforeAll
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }


    @Test
    public void test_json(){

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty.
                withHost("localhost").withPort(123).withResource(new FactoryTemplateId<>(DummyResourceFactory.class))
        );

        builder.addSingleton(DummyResourceFactory.class);

        System.out.println(ObjectMapperBuilder.build().writeValueAsString(builder.buildTree()));

//        ObjectMapperBuilder.build().copy(serverFactory);
    }

    public static  class SpecialObjectMapperFactory extends SimpleFactoryBase<ObjectMapper, JettyServerRootFactory> {
        @Override
        protected ObjectMapper createImpl() {
            return ObjectMapperBuilder.buildNewObjectMapper();
        }
    }



    @Test
    public void test_ObjectMapper(){
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(DummyResourceFactory.class)).withObjectMapper(new SpecialObjectMapperFactory())
        );

//        ObjectMapperBuilder.build().copy(serverFactory);
    }


    public static class HelloWorldHandler extends Handler.Abstract {

        @Override
        public boolean handle(Request request, Response response, Callback callback) throws Exception {
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            response.write(true, UTF_8.encode("Hello World"), callback);

            return true;
        }
    }
    @Test
    public void test_handler() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withHandlerFirst(AttributelessFactory.create(HelloWorldHandler.class))
        );


        builder.addFactory(JerseyServletFactoryTest.JerseyServletTestErrorResourceFactory.class, Scope.SINGLETON);


        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();
        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/test")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals("Hello World", response.body());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }

    @Path("/")
    public static class TestResource {
        private final String text;
        public TestResource(String text) {
            this.text = text;
        }

        @GET
        public String test() {
            return text;
        }
    }

    public static class TestResourceFactory extends SimpleFactoryBase<TestResource, MultiJettyServerRootFactory> {
        public final StringAttribute text = new StringAttribute();
        @Override
        protected TestResource createImpl() {
            return new TestResource(text.get());
        }
    }

    @Test
    public void test_multiple_server() {
        MultiJettyFactoryTreeBuilder builder = new MultiJettyFactoryTreeBuilder();
        builder.addJetty("Jetty1",(jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(new FactoryTemplateId<>(TestResourceFactory.class,"1"))
        );
        builder.addJetty("Jetty2",(jetty, ctx)-> jetty.
                withHost("localhost").withPort(8088).withResource(new FactoryTemplateId<>(TestResourceFactory.class,"2"))
        );

        builder.addFactory(TestResourceFactory.class,"1", Scope.SINGLETON, ctx->{
            TestResourceFactory test = new TestResourceFactory();
            test.text.set("1");
            return test;
        });
        builder.addFactory(TestResourceFactory.class,"2", Scope.SINGLETON, ctx->{
            TestResourceFactory test = new TestResourceFactory();
            test.text.set("2");
            return test;
        });

        Microservice<List<Server>, MultiJettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();

        try {
            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("1", response.body());
            }

            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8088")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("2", response.body());
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_pathSpec_validation() {
        MultiJettyFactoryTreeBuilder builder = new MultiJettyFactoryTreeBuilder();
        builder.addJetty("Jetty1",(jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087)
                .withJersey(rb->rb.withPathSpec("/test").withResource(new FactoryTemplateId<>(TestResourceFactory.class,"1")),new FactoryTemplateName("1"))
                .withJersey(rb->rb.withPathSpec("/test").withResource(new FactoryTemplateId<>(TestResourceFactory.class,"2")),new FactoryTemplateName("2"))
        );

        builder.addFactory(TestResourceFactory.class,"1", Scope.SINGLETON, ctx->{
            TestResourceFactory test = new TestResourceFactory();
            test.text.set("1");
            return test;
        });
        builder.addFactory(TestResourceFactory.class,"2", Scope.SINGLETON, ctx->{
            TestResourceFactory test = new TestResourceFactory();
            test.text.set("2");
            return test;
        });


        Assertions.assertThrows(IllegalStateException.class, builder::buildTree);
    }




    public static class Test2ResourceFactory extends SimpleFactoryBase<TestResource, JettyServerRootFactory> {
        @Override
        protected TestResource createImpl() {
            return new TestResource("1");
        }
    }


    @Test
    public void test_multiple_connectors() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty,ctx)->{
            jetty
            .withResource(new FactoryTemplateId<>(Test2ResourceFactory.class))
            .withAdditionalConnector(connector-> connector.withHost("localhost").withPort(8087),new FactoryTemplateName("connector1"))
            .withAdditionalConnector(connector-> connector.withHost("localhost").withPort(8088),new FactoryTemplateName("connector2"));

        });
        builder.addFactory(Test2ResourceFactory.class,Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();

        try {
            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("1", response.body());
            }

            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8088")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("1", response.body());
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_error_response_for_no_path_match() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty,ctx)->{
            jetty.withPort(8087).withHost("localhost").
            withJersey(resourceBuilder ->
                    resourceBuilder.withResource(new FactoryTemplateId<>(Test2ResourceFactory.class)).withPathSpec("/test123/*")
                    , new FactoryTemplateName("Test123"));
        });
        builder.addFactory(Test2ResourceFactory.class,Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        microservice.start();

        try {
            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087/test123")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals("1", response.body());
            }

            {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8087")).build();
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