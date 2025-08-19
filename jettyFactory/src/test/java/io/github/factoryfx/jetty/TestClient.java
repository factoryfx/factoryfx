package io.github.factoryfx.jetty;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;

import java.net.URI;

public class TestClient implements AutoCloseable {

    private final String host;
    private final int port;

    private final Client client;

    public TestClient(String host, int port) {
        this.host = host;
        this.port = port;

        ClientConfig cc = new ClientConfig();
        this.client = ClientBuilder.newBuilder().withConfig(cc).build();
    }

    public <RES> RES get(String uri, Class<RES> responseClass) {
        return this.client.target(buildUrl(uri)).request().buildGet().invoke(responseClass);
    }

    public <RES> RES post(String uri, Entity<?> entity, Class<RES> responseClass) {
        return this.client.target(buildUrl(uri)).request().buildPost(entity).invoke(responseClass);
    }


    public URI buildUrl(String uri) {
        return UriBuilder.newInstance().scheme("http").host(host).port(port).uri(uri).build();
    }


    @Override
    public void close() {
        this.client.close();
    }
}
