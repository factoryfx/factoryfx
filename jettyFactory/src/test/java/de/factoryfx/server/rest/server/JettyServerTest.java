package de.factoryfx.server.rest.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.factoryfx.server.rest.client.RestClient;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
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
        List<Function<Server,ServerConnector>> connectors= new ArrayList<>();
        connectors.add(server -> {
            final NetworkTrafficServerConnector networkTrafficServerConnector = new NetworkTrafficServerConnector(server);
            networkTrafficServerConnector.setHost("localhost");
            networkTrafficServerConnector.setPort(8005);
            return networkTrafficServerConnector;

        });
        JettyServer jettyServer = new JettyServer(connectors, Arrays.asList(new Resource1(), new Resource2()));
        jettyServer.start();
//        Thread.sleep(1000);

        RestClient restClient = new RestClient("localhost",8005,"",false,null,null);
        System.out.println(restClient.get("Resource1",String.class));
        System.out.println(restClient.get("Resource2",String.class));
    }

}