package de.factoryfx.jetty;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.MicroserviceBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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

    public static class Resource1Factory extends SimpleFactoryBase<Resource1,Void, JettyServerRootFactory> {
        @Override
        public Resource1 createImpl() {
            return new Resource1();
        }
    }

    public static class JettyServerRootFactory extends SimpleFactoryBase<Server,Void, JettyServerRootFactory>{
        @SuppressWarnings("unchecked")
        public final FactoryReferenceAttribute<Server,JettyServerFactory<Void, JettyServerRootFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

        @Override
        public Server createImpl() {
            return server.instance();
        }
    }

    @BeforeClass
    public static void setup(){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_change_port() throws InterruptedException {
        FactoryTreeBuilder<JettyServerRootFactory> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class);
        builder.addFactory(JettyServerRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,JettyServerRootFactory>())
                    .withHost("localhost").widthPort(8080)
                    .withResource(ctx.get(Resource1Factory.class)).build();
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Void, Server, JettyServerRootFactory, Object> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder);
        try {
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.assertEquals(200, response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataAndNewMetadata<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().connectorManager.get().connectors.get(0).port.set(8081);
            microservice.updateCurrentFactory(update, "", "", (p) -> true);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8081/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.assertEquals(200, response.statusCode());
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
        FactoryTreeBuilder<JettyServerRootFactory> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class);
        builder.addFactory(JettyServerRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,JettyServerRootFactory>())
                    .withHost("localhost").widthPort(8080)
                    .withResource(ctx.get(Resource1Factory.class)).build();
        });
        builder.addFactory(Resource1Factory.class, Scope.SINGLETON, ctx -> {
            return new Resource1Factory();
        });
        Microservice<Void, Server, JettyServerRootFactory, Object> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder);
        try{
            microservice.start();

            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.assertEquals(200,response.statusCode());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            DataAndNewMetadata<JettyServerRootFactory> update = microservice.prepareNewFactory();
            update.root.server.get().connectorManager.get().connectors.clear();
            microservice.updateCurrentFactory(update,"","",(p)->true);


            try {
                HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/Resource1")).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Assert.fail("expect ConnectException");
            } catch (IOException | InterruptedException e) {
                //expected
            }

        } finally {
            microservice.stop();
        }
    }


//    @Path("/Resource1")
//    public static class Resource1{
//        @GET()
//        public Response get(){
//            return Response.ok().build();
//        }
//    }
//
//    @Path("/Resource2")
//    public static class Resource2{
//        @GET()
//        public Response get(){
//            return Response.ok().build();
//        }
//    }
//
//    private ServletBuilder servlets(Object ... resources) {
//        ServletBuilder servletBuilder = new ServletBuilder();
//        servletBuilder.withJerseyResources("/*",List.of(resources));
//        return servletBuilder;
//    }
//
//    @Test
//    public void test_multiple_resources_samepath() throws InterruptedException {
//        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
//        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
//        JettyServer jettyServer = new JettyServer(connectors,servlets(new Resource1(), new Resource2()));
//        jettyServer.start();
////        Thread.sleep(1000);
//
//        System.out.println(get(8015,"Resource1"));
//        System.out.println(get(8015,"Resource2"));
//        jettyServer.stop();
//    }
//
//    @Path("/Resource")
//    public static class Resource {
//        final String answer;
//
//        public Resource(String answer) {
//            this.answer = answer;
//        }
//
//        @GET()
//        public Response get(){
//            return Response.ok(answer).build();
//        }
//    }
//
//    @Test
//    public void test_recreate() throws InterruptedException {
//        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
//        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
//        JettyServer jettyServer = new JettyServer(connectors, servlets(new Resource("Hello")));
//        jettyServer.start();
////        Thread.sleep(1000);
//
//        Assert.assertEquals("Hello",get(8015,"Resource"));
//        jettyServer = jettyServer.recreate(connectors,servlets(new Resource("World")));
//        Assert.assertEquals("World",get(8015,"Resource"));
//        jettyServer.stop();
//
//    }
//

//
//
//    @Path("/Resource")
//    public static class LateResponseTestResource {
//
//        public LateResponseTestResource() {
//
//        }
//
//        @GET()
//        public Response get() throws InterruptedException {
//            System.out.println("IN");
//            try {
//                Thread.sleep(500);
//                return Response.ok("RESPONSE").build();
//            } finally {
//                System.out.println("Out");
//            }
//        }
//    }
//
//
//    @Test
//    public void test_lateResponse() throws InterruptedException, ExecutionException, TimeoutException {
//        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
//        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
//        JettyServer jettyServer = new JettyServer(connectors, servlets(new LateResponseTestResource()));
//        jettyServer.start();
//
//        try {
//            CompletableFuture<String> lateResponse = new CompletableFuture<>();
//            new Thread() {
//                public void run() {
//                    try {
//                        lateResponse.complete(get(8015,"Resource"));
//                    } catch (Exception ex) {
//                        lateResponse.completeExceptionally(ex);
//                    }
//                }
//            }.start();
//            Thread.sleep(400);
//            jettyServer = jettyServer.recreate(connectors,servlets());
//            try {
//                get(8015,"Resource");
//                Assert.fail("Expected exception");
//            } catch (Exception expected) {}
//            Assert.assertEquals("RESPONSE",lateResponse.get(1000, TimeUnit.MILLISECONDS));
//
//        } finally {
//            jettyServer.stop();
//        }
//
//
//    }
//
//    @Path("/Killer")
//    public static class ResourceKiller{
//        private  Runnable stopper;
//
//        private final Consumer<Thread> killerThread;
//
//        public ResourceKiller(Consumer<Thread> killerThread) {
//            this.killerThread = killerThread;
//        }
//
//        public void setStopper(Runnable stopper){
//            this.stopper = stopper;
//        }
//
//
//        @GET()
//        public Response get(){
//            killerThread.accept(Thread.currentThread());
//            stopper.run();
//            return Response.ok().build();
//        }
//    }
//
//    @Test
//    public void test_stop_itself() throws InterruptedException {
//        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
//        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
//        final Thread whichThread[] = new Thread[1];
//        ResourceKiller killer = new ResourceKiller(t->{
//            whichThread[0] = t;
//        });
//        JettyServer jettyServer = new JettyServer(connectors, servlets(killer));
//        jettyServer.start();
//
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        killer.setStopper(()->{
//            jettyServer.stop();
//            try {
//                Thread.sleep(10);//some code that trigger interrupted exception if thread is already interrupted(unwanted)
//                future.complete(null);
//            } catch (InterruptedException e) {
//                future.completeExceptionally(e);            }
//        });
//
//        try {
//            get(8015,"Killer");
//            Assert.fail("Expected exception");
//        } catch (Exception expected) {
//            expected.printStackTrace();
//        }
//        try {
//            future.get();
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            whichThread[0].join(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        Assert.assertFalse(whichThread[0].isAlive());
//        Assert.assertTrue(jettyServer.isStopped());
//
//
//
//    }
//
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

    public static final class MessageBodyReaderWriterEchoFactory extends SimpleFactoryBase<MessageBodyReaderWriterEcho,Void,JettyServerRootFactory> {
        @Override
        public MessageBodyReaderWriterEcho createImpl() {
            return new MessageBodyReaderWriterEcho();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMessageBodyReader() {
        FactoryTreeBuilder<JettyServerRootFactory> builder = new FactoryTreeBuilder<>(JettyServerRootFactory.class);
        builder.addFactory(JettyServerRootFactory.class, Scope.SINGLETON);
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx->{
            return new JettyServerBuilder<>(new JettyServerFactory<Void,JettyServerRootFactory>())
                    .withHost("localhost").widthPort(8015)
                    .withResource(ctx.get(MessageBodyReaderWriterEchoFactory.class))
                    .withJaxrsComponent(SomeMessageBodyReaderWriter.class)
                    .build();
        });
        builder.addFactory(MessageBodyReaderWriterEchoFactory.class, Scope.SINGLETON);

        Microservice<Void, Server, JettyServerRootFactory, Object> microservice = MicroserviceBuilder.buildInMemoryMicroservice(builder);
        try{
            microservice.start();

            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            String resp = client.target("http://localhost:8015/echo").request().buildPost(Entity.entity("Hello", MediaType.valueOf("my/mime"))).invoke().readEntity(String.class);

            Assert.assertEquals("Changed by writer: Changed by reader: Hello", resp);

        } finally {
            microservice.stop();
        }
    }


}