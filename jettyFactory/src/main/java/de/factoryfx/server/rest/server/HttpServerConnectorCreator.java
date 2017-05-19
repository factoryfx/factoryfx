package de.factoryfx.server.rest.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class HttpServerConnectorCreator {

    private final String host;
    private final int port;

    public HttpServerConnectorCreator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void addToServer(Server server) {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof NetworkTrafficServerConnector) {
                NetworkTrafficServerConnector serverConnector = (NetworkTrafficServerConnector)connector;
                if (serverConnector.getPort() == port && serverConnector.getHost() == host)
                    return;
            }
        }
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(port);
        connector.setReuseAddress(true);
        connector.setHost(host);
        server.addConnector(connector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpServerConnectorCreator that = (HttpServerConnectorCreator) o;

        if (port != that.port) return false;
        return host.equals(that.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    public void removeFromServer(Server server) {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof NetworkTrafficServerConnector) {
                NetworkTrafficServerConnector serverConnector = (NetworkTrafficServerConnector)connector;
                if (serverConnector.getPort() == port && serverConnector.getHost() == host) {
                    server.removeConnector(serverConnector);
                }
            }
        }
    }
}
