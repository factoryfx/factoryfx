package de.factoryfx.jetty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import javax.management.relation.RoleUnresolved;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

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
    @Test
    public void test_multiple_resources_samepath() throws InterruptedException {
        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8015,null));
        JettyServer jettyServer = new JettyServer(connectors, Arrays.asList(new Resource1(), new Resource2()));
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
        JettyServer jettyServer = new JettyServer(connectors, Arrays.asList(new Resource("Hello")));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8015,"",false,null,null);
        Assert.assertEquals("Hello",restClient.get("Resource",String.class));
        jettyServer = jettyServer.recreate(connectors,Arrays.asList(new Resource("World")));
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
        List<Object> resources = Arrays.asList(new Resource("Hello"));

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
        JettyServer jettyServer = new JettyServer(connectors, List.of(new LateResponseTestResource()));
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
            jettyServer = jettyServer.recreate(connectors,new ArrayList<>());
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
        JettyServer jettyServer = new JettyServer(connectors, List.of(killer));
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

}