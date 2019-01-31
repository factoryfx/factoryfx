package de.factoryfx.jetty;

import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnector {

    private final String host;
    private final int port;
    private final SslContextFactory sslContextFactory;

    private  NetworkTrafficServerConnector connector;

    public HttpServerConnector(String host, int port, SslContextFactory sslContextFactory) {
        this.host = host;
        this.port = port;
        this.sslContextFactory= sslContextFactory;
    }

    public void addToServer(Server server) {
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

    public void removeFromServer() {
        connector.getServer().removeConnector(connector);
    }
}
