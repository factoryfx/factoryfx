package io.github.factoryfx.jetty;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class HttpServerConnector {

    private final String host;
    private final int port;
    private final SslContextFactory sslContextFactory;
    private final HttpConfiguration httpConfiguration;


    private  NetworkTrafficServerConnector connector;

    public HttpServerConnector(String host, int port, SslContextFactory sslContextFactory, HttpConfiguration httpConfiguration) {
        this.host = host;
        this.port = port;
        this.sslContextFactory= sslContextFactory;
        if (httpConfiguration==null){
            this.httpConfiguration=new HttpConfiguration();
        } else {
            this.httpConfiguration = httpConfiguration;
        }
    }

    public void addToServer(Server server) {
        if (sslContextFactory!=null){
            connector = new NetworkTrafficServerConnector(server,new HttpConnectionFactory(httpConfiguration),sslContextFactory);
        } else {
            connector = new NetworkTrafficServerConnector(server,new HttpConnectionFactory(httpConfiguration));
        }

        connector.setPort(port);
        connector.setReuseAddress(true);
        connector.setHost(host);
        server.addConnector(connector);
        server.manage(connector);
        if (server.isStarted()) {
            try {
                connector.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
