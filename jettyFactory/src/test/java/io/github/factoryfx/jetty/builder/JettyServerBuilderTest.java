package io.github.factoryfx.jetty.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.jetty.JerseyServletFactoryTest;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class JettyServerBuilderTest {

    public static class DummyResource extends SimpleFactoryBase<Void, JettyServerRootFactory> {

        @Override
        protected Void createImpl() {
            return null;
        }
    }


    @Test
    public void test_json(){

        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty.
                withHost("localhost").withPort(123).withResource(new DummyResource())
        );

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
                withHost("localhost").withPort(8087).withResource(ctx.get(DummyResource.class)).withObjectMapper(new SpecialObjectMapperFactory())
        );

//        ObjectMapperBuilder.build().copy(serverFactory);
    }


    public static class HelloWorldHandler extends AbstractHandler {
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().print("HelloWorld");
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
            Assertions.assertEquals("HelloWorld", response.body());
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
                withHost("localhost").withPort(8087).withResource(ctx.get(TestResourceFactory.class,"1"))
        );
        builder.addJetty("Jetty2",(jetty, ctx)-> jetty.
                withHost("localhost").withPort(8088).withResource(ctx.get(TestResourceFactory.class,"2"))
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
                .withJersey(rb->rb.withPathSpec("/test").withResource(ctx.get(TestResourceFactory.class,"1")),"1")
                .withJersey(rb->rb.withPathSpec("/test").withResource(ctx.get(TestResourceFactory.class,"2")),"2")
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
            .withResource(ctx.get(Test2ResourceFactory.class))
            .withAdditionalConnector(connector-> connector.withHost("localhost").withPort(8087),"connector1")
            .withAdditionalConnector(connector-> connector.withHost("localhost").withPort(8088),"connector2");

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

}