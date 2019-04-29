package io.github.factoryfx.docu.rule.server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class BackendClient {

    private final int backendPort;

    public BackendClient(int backendPort) {
        this.backendPort = backendPort;
    }

    public String hello(){

        Client backendClient = ClientBuilder.newClient();
        WebTarget webTarget = backendClient.target("http://localhost:" + backendPort + "/hello/hello");
        String hello = webTarget.request(MediaType.TEXT_PLAIN).get(String.class);

        return hello;
    }
}
