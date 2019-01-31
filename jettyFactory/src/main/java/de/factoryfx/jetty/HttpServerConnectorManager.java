package de.factoryfx.jetty;

import org.eclipse.jetty.server.Server;

import java.util.List;

public class HttpServerConnectorManager {

    private final List<HttpServerConnector> connectors;
    private Server server;

    public HttpServerConnectorManager(List<HttpServerConnector> connectors){
        this.connectors=connectors;
    }

    public void addToServer(Server server){
        this.server= server;
        connectors.forEach(httpServerConnector -> httpServerConnector.addToServer(server));
    }

    public void update(List<HttpServerConnector> newConnectors){
        this.connectors.forEach(HttpServerConnector::removeFromServer);
        this.connectors.clear();
        this.connectors.addAll(newConnectors);
        this.connectors.forEach(httpServerConnector -> httpServerConnector.addToServer(server));
    }
}
