package de.factoryfx.server.rest.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import de.factoryfx.server.rest.client.RestClient;
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
        connectors.add(new HttpServerConnectorCreator("localhost",8005));
        JettyServer jettyServer = new JettyServer(connectors, Arrays.asList(new Resource1(), new Resource2()));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8005,"",false,null,null);
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
        connectors.add(new HttpServerConnectorCreator("localhost",8005));
        JettyServer jettyServer = new JettyServer(connectors, Arrays.asList(new Resource("Hello")));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8005,"",false,null,null);
        Assert.assertEquals("Hello",restClient.get("Resource",String.class));
        jettyServer = jettyServer.recreate(connectors,Arrays.asList(new Resource("World")));
        Assert.assertEquals("World",restClient.get("Resource",String.class));
        jettyServer.stop();

    }

    @Test
    public void test_addRemoveConnector() throws InterruptedException {

        List<HttpServerConnectorCreator> connectors= new ArrayList<>();
        connectors.add(new HttpServerConnectorCreator("localhost",8005));
        List<HttpServerConnectorCreator> moreConnectors= new ArrayList<>();
        moreConnectors.add(new HttpServerConnectorCreator("localhost",8005));
        moreConnectors.add(new HttpServerConnectorCreator("localhost",8006));
        List<Object> resources = Arrays.asList(new Resource("Hello"));

        JettyServer jettyServer = new JettyServer(connectors, resources);
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient8005 = new RestClient("localhost",8005,"",false,null,null);
        RestClient restClient8006 = new RestClient("localhost",8006,"",false,null,null);
        Assert.assertEquals("Hello",restClient8005.get("Resource",String.class));
        try {
            restClient8006.get("Resource",String.class);
            Assert.fail("Expectected exception");
        } catch (Exception expected) {}

        jettyServer = jettyServer.recreate(moreConnectors,resources);
        Assert.assertEquals("Hello",restClient8005.get("Resource",String.class));
        Assert.assertEquals("Hello",restClient8006.get("Resource",String.class));

        jettyServer = jettyServer.recreate(connectors,resources);
        Assert.assertEquals("Hello",restClient8005.get("Resource",String.class));
        try {
            restClient8006.get("Resource",String.class);
            Assert.fail("Expectected exception");
        } catch (Exception expected) {}
        jettyServer.stop();

    }

}