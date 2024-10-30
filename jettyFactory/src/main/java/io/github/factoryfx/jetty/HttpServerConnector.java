package io.github.factoryfx.jetty;

import java.util.Objects;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class HttpServerConnector {

    private final String host;
    private final int port;
    private final SslContextFactory.Server sslContextFactory;
    private final HttpConfiguration httpConfiguration;

    private final boolean useHttp2;

    public HttpServerConnector(String host, int port, SslContextFactory.Server sslContextFactory, HttpConfiguration httpConfiguration, boolean useHttp2) {
        this.host = host;
        this.port = port;
        this.sslContextFactory= sslContextFactory;
        this.httpConfiguration = Objects.requireNonNullElseGet(httpConfiguration, HttpConfiguration::new);
        this.useHttp2=useHttp2;
    }

    public void addToServer(Server server) {

        ServerConnector connector;
        if (useHttp2) {
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            HttpConnectionFactory h1 = new HttpConnectionFactory(httpConfiguration);
            HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpConfiguration);
            ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
            alpn.setDefaultProtocol(h1.getProtocol());
            if (sslContextFactory!=null){
                connector = new ServerConnector(server, sslContextFactory, alpn, h2, new HttpConnectionFactory(httpConfiguration));
            } else {
                connector = new ServerConnector(server, alpn, h1, h2);
            }
        } else {
            if (sslContextFactory!=null){
                connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfiguration), sslContextFactory);
            } else {
                connector = new NetworkTrafficServerConnector(server, new HttpConnectionFactory(httpConfiguration));
            }
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
