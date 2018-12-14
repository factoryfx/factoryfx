package de.factoryfx.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnectorCreator {

    private final String host;
    private final int port;
    private final SslContextFactory sslContextFactory;

    public HttpServerConnectorCreator(String host, int port, SslContextFactory sslContextFactory) {
        this.host = host;
        this.port = port;
        this.sslContextFactory= sslContextFactory;
    }

    public void addToServer(Server server) {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof NetworkTrafficServerConnector) {
                NetworkTrafficServerConnector serverConnector = (NetworkTrafficServerConnector)connector;
                if (serverConnector.getPort() == port && serverConnector.getHost().equals(host))
                    return;
            }
        }

        NetworkTrafficServerConnector connector;
        if (sslContextFactory!=null){
            connector = new NetworkTrafficServerConnector(server,sslContextFactory);
        } else {
            connector = new NetworkTrafficServerConnector(server);
        }

        connector.setPort(port);
        connector.setReuseAddress(true);
        connector.setHost(host);
        server.addConnector(connector);
        if (server.isStarted()) {
            try {
                connector.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpServerConnectorCreator that = (HttpServerConnectorCreator) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return sslContextFactory != null ? sslContextFactory.equals(that.sslContextFactory) : that.sslContextFactory == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (sslContextFactory != null ? sslContextFactory.hashCode() : 0);
        return result;
    }

    public void removeFromServer(Server server) {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof NetworkTrafficServerConnector) {
                NetworkTrafficServerConnector serverConnector = (NetworkTrafficServerConnector)connector;
                if (serverConnector.getPort() == port && serverConnector.getHost().equals(host)) {
                    server.removeConnector(serverConnector);
                    if (server.isStarted()) {
                        try {
                            serverConnector.stop();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
