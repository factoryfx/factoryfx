package de.factoryfx.jetty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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

import com.google.common.io.ByteStreams;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Assert;
import org.junit.Test;

public class JettyServerTest {
    @Path("/Resource1")
    public static class Resource1{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    @Path("/Resource2")
    public static class Resource2{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }
    
    private ServletBuilder servlets(Object ... resources) {
        ServletBuilder servletBuilder = new ServletBuilder();
        return servletBuilder.withJerseyResources("/*",Arrays.asList(resources));
    }
    
    @Test
    public void test_multiple_resources_samepath() throws InterruptedException {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        JettyServer jettyServer = new JettyServer(connectors,servlets(new Resource1(), new Resource2()));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8015,"",false,null,null);
        System.out.println(restClient.get("Resource1",String.class));
        System.out.println(restClient.get("Resource2",String.class));
        jettyServer.stop();
    }

    @Path("/Resource")
    public static class Resource {
        final String answer;

        public Resource(String answer) {
            this.answer = answer;
        }

        @GET()
        public Response get(){
            return Response.ok(answer).build();
        }
    }

    @Test
    public void test_recreate() throws InterruptedException {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        JettyServer jettyServer = new JettyServer(connectors, servlets(new Resource("Hello")));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8015,"",false,null,null);
        Assert.assertEquals("Hello",restClient.get("Resource",String.class));
        jettyServer = jettyServer.recreate(connectors,servlets(new Resource("World")));
        Assert.assertEquals("World",restClient.get("Resource",String.class));
        jettyServer.stop();

    }

    @Test
    public void test_addRemoveConnector() throws InterruptedException {

        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        List<HttpServerConnectorCreator> moreConnectors= new ArrayList<>();
        moreConnectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        moreConnectors.add(new HttpServerConnectorCreator("localhost",8016,null));
        ServletBuilder resources = servlets(new Resource("Hello"));

        JettyServer jettyServer = new JettyServer(connectors, resources);
        jettyServer.start();
//        Thread.sleep(1000);


        RestClient restClient8015 = new RestClient("localhost",8015,"",false,null,null);
        RestClient restClient8016 = new RestClient("localhost",8016,"",false,null,null);
        Assert.assertEquals("Hello",restClient8015.get("Resource",String.class));
        try {
            restClient8016.get("Resource",String.class);
            Assert.fail("Expectected exception");
        } catch (Exception expected) {}

        jettyServer = jettyServer.recreate(moreConnectors,resources);
        Assert.assertEquals("Hello",restClient8015.get("Resource",String.class));
        Assert.assertEquals("Hello",restClient8016.get("Resource",String.class));

        jettyServer = jettyServer.recreate(connectors,resources);
        Assert.assertEquals("Hello",restClient8015.get("Resource",String.class));
        try {
            restClient8016.get("Resource",String.class);
            Assert.fail("Expectected exception");
        } catch (Exception expected) {}
        jettyServer.stop();

    }


    @Path("/Resource")
    public static class LateResponseTestResource {

        public LateResponseTestResource() {

        }

        @GET()
        public Response get() throws InterruptedException {
            System.out.println("IN");
            try {
                Thread.sleep(500);
                return Response.ok("RESPONSE").build();
            } finally {
                System.out.println("Out");
            }
        }
    }


    @Test
    public void test_lateResponse() throws InterruptedException, ExecutionException, TimeoutException {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        JettyServer jettyServer = new JettyServer(connectors, servlets(new LateResponseTestResource()));
        jettyServer.start();

        try {
            RestClient restClient = new RestClient("localhost",8015,"",false,null,null);
            CompletableFuture<String> lateResponse = new CompletableFuture<>();
            new Thread() {
                public void run() {
                    try {
                        lateResponse.complete(restClient.get("Resource", String.class));
                    } catch (Exception ex) {
                        lateResponse.completeExceptionally(ex);
                    }
                }
            }.start();
            Thread.sleep(400);
            jettyServer = jettyServer.recreate(connectors,servlets());
            try {
                restClient.get("Resource",String.class);
                Assert.fail("Expected exception");
            } catch (Exception expected) {}
            Assert.assertEquals("RESPONSE",lateResponse.get(1000, TimeUnit.MILLISECONDS));

        } finally {
            jettyServer.stop();
        }


    }

    @Path("/Killer")
    public static class ResourceKiller{
        private  Runnable stopper;

        private final Consumer<Thread> killerThread;

        public ResourceKiller(Consumer<Thread> killerThread) {
            this.killerThread = killerThread;
        }

        public void setStopper(Runnable stopper){
            this.stopper = stopper;
        }


        @GET()
        public Response get(){
            killerThread.accept(Thread.currentThread());
            stopper.run();
            return Response.ok().build();
        }
    }

    @Test
    public void test_stop_itself() throws InterruptedException {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        final Thread whichThread[] = new Thread[1];
        ResourceKiller killer = new ResourceKiller(t->{
            whichThread[0] = t;
        });
        JettyServer jettyServer = new JettyServer(connectors, servlets(killer));
        jettyServer.start();

        CompletableFuture<Void> future = new CompletableFuture<>();
        killer.setStopper(()->{
            jettyServer.stop();
            try {
                Thread.sleep(10);//some code that trigger interrupted exception if thread is already interrupted(unwanted)
                future.complete(null);
            } catch (InterruptedException e) {
                future.completeExceptionally(e);            }
        });

        RestClient restClient = new RestClient("localhost",8015,"",false,null,null);
        try {
            restClient.get("Killer", String.class);
            Assert.fail("Expected exception");
        } catch (ProcessingException expected) {
            expected.printStackTrace();
        }
        try {
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        try {
            whichThread[0].join(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertFalse(whichThread[0].isAlive());
        Assert.assertTrue(jettyServer.isStopped());



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

    };

    @Test
    public void testMessageBodyReader() {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        ServletBuilder servletBuilder = new ServletBuilder();

        boolean working = false;

        if (working) {
            ResourceConfig resourceConfig = new ResourceConfig();
            resourceConfig.register(SomeMessageBodyReaderWriter.class);
            resourceConfig.register(new MessageBodyReaderWriterEcho());
            servletBuilder.withServlet("/*", new ServletContainer(resourceConfig));
        } else {
            servletBuilder.withJerseyResource(SomeMessageBodyReaderWriter.class);
            servletBuilder.withJerseyResources("/*", List.of(new MessageBodyReaderWriterEcho()));
        }
        JettyServer jettyServer = new JettyServer(connectors, servletBuilder);
        jettyServer.start();

        try {
            ClientConfig cc = new ClientConfig();
            Client client = ClientBuilder.newBuilder().withConfig(cc).build();
            String resp = client.target("http://localhost:8015/echo").request().buildPost(Entity.entity("Hello", MediaType.valueOf("my/mime"))).invoke().readEntity(String.class);

            Assert.assertEquals("Changed by writer: Changed by reader: Hello", resp);
        } finally {
            jettyServer.stop();
        }

    }

}