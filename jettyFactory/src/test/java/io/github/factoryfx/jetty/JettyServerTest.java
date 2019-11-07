package io.github.factoryfx.jetty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import ch.qos.logback.classic.Level;
import com.google.common.io.ByteStreams;
import io.github.factoryfx.factory.AttributelessFactory;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.JettyFactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.server.Microservice;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServerTest {
    @Path("/Resource1")
    public static class Resource1{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    public static class Resource1Factory extends SimpleFactoryBase<Resource1, JettyServerRootFactory> {
        @Override
        protected Resource1 createImpl() {
            return new Resource1();
        }
    }

    @BeforeAll
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_change_port() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)-> jetty.
                withHost("localhost").withPort(8087).withResource(ctx.get(Resource1Factory.class))
        );

        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try {
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200, response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.connectors.get(0).port.set(8081);
            microservice.updateCurrentFactory(update);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200, response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            microservice.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_remove_connector() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withResource(ctx.get(Resource1Factory.class)
            );
        });


        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(200,response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.connectors.clear();
            microservice.updateCurrentFactory(update);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8087/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

        } finally {
            microservice.stop();
        }
    }

    @Path("/Resource")
    public static class LateResponseTestResource {

        public LateResponseTestResource() {

        }

        @GET()
        public Response get() throws InterruptedException {
            Thread.sleep(400);
            return Response.ok("RESPONSE").build();
        }
    }

    public static class LateResponseTestResourceFactory extends SimpleFactoryBase<LateResponseTestResource, JettyServerRootFactory> {
        @Override
        protected LateResponseTestResource createImpl() {
            return new LateResponseTestResource();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_lateResponse() throws InterruptedException, ExecutionException, TimeoutException {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withResource(ctx.get(LateResponseTestResourceFactory.class)
            );
        });
        builder.addFactory(LateResponseTestResourceFactory.class, Scope.SINGLETON, ctx -> {
            return new LateResponseTestResourceFactory();
        });
        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();

        try {
            microservice.start();
            CompletableFuture<String> lateResponse = new CompletableFuture<>();
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8015/Resource")).GET().build();
            new Thread() {
                public void run() {
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        lateResponse.complete(response.body());
                    } catch (Exception ex) {
                        lateResponse.completeExceptionally(ex);
                    }
                }
            }.start();
            Thread.sleep(200);
            DataUpdate<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.clearResource(LateResponseTestResourceFactory.class);
            microservice.updateCurrentFactory(update);
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assertions.assertEquals(404,response.statusCode());
            } catch (Exception expected) {}
            Assertions.assertEquals("RESPONSE",lateResponse.get(1000, TimeUnit.MILLISECONDS));

        } finally {
            microservice.stop();
        }
    }

    static final class MyMime {
        public String data;
    }


    @Produces("my/mime")
    @Consumes("my/mime")
    public static final class SomeMessageBodyReaderWriter implements MessageBodyWriter<MyMime>, MessageBodyReader<MyMime> {

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return MyMime.class.isAssignableFrom(type);
        }

        @Override
        public MyMime readFrom(Class<MyMime> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteStreams.copy(entityStream, out);
            MyMime ret = new MyMime();
            ret.data = "Changed by reader: "+out.toString(StandardCharsets.UTF_8);
            return ret;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return MyMime.class.isAssignableFrom(type);
        }

        @Override
        public void writeTo(MyMime s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write(("Changed by writer: "+s.data).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Path("/echo")
    public static final class MessageBodyReaderWriterEcho {

        @POST
        public MyMime echo(MyMime req) {
            return req;
        }

    }

    public static final class MessageBodyReaderWriterEchoFactory extends SimpleFactoryBase<MessageBodyReaderWriterEcho, JettyServerRootFactory> {
        @Override
        protected MessageBodyReaderWriterEcho createImpl() {
            return new MessageBodyReaderWriterEcho();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMessageBodyReader() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8015).withResource(ctx.get(MessageBodyReaderWriterEchoFactory.class))
                        .withJaxrsComponent(AttributelessFactory.create(SomeMessageBodyReaderWriter.class)
                    );
        });

        builder.addFactory(MessageBodyReaderWriterEchoFactory.class, Scope.SINGLETON);

        Microservice<Server, JettyServerRootFactory> microservice = builder.microservice().build();
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            String resp = client.target("http://localhost:8015/echo").request().buildPost(Entity.entity("Hello", MediaType.valueOf("my/mime"))).invoke().readEntity(String.class);

            Assertions.assertEquals("Changed by writer: Changed by reader: Hello", resp);

        } finally {
            microservice.stop();
        }
    }

    @Test
    public void test_custom_jersey() {
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
            jetty.withHost("localhost").withPort(8087).withJersey(rb->rb.withResource(ctx.get(Resource1Factory.class)),"/new/*");
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
    }




}