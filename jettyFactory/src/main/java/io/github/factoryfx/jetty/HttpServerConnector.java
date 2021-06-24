package io.github.factoryfx.jetty;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class HttpServerConnector {

    private final String host;
    private final int port;
    private final SslContextFactory sslContextFactory;
    private final HttpConfiguration httpConfiguration;


    private ServerConnector connector;
    private boolean useHttp2;

    public HttpServerConnector(String host, int port, SslContextFactory sslContextFactory, HttpConfiguration httpConfiguration, boolean useHttp2) {
        this.host = host;
        this.port = port;
        this.sslContextFactory= sslContextFactory;
        if (httpConfiguration==null){
            this.httpConfiguration=new HttpConfiguration();
        } else {
            this.httpConfiguration = httpConfiguration;
        }
        this.useHttp2=useHttp2;
    }

    public void addToServer(Server server) {


        if (useHttp2) {
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            HttpConnectionFactory h1 = new HttpConnectionFactory(httpConfiguration);
            HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpConfiguration);
            ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
            alpn.setDefaultProtocol(h1.getProtocol());
            connector = new ServerConnector(server,sslContextFactory,alpn,h1,h2);
            if (sslContextFactory!=null){
                connector = new ServerConnector(server, sslContextFactory, alpn, h2, new HttpConnectionFactory(httpConfiguration));
            } else {
                connector = new ServerConnector(server, alpn, h1, h2);
            }
        } else {
            if (sslContextFactory!=null){
                connector = new NetworkTrafficServerConnector(server,new HttpConnectionFactory(httpConfiguration),sslContextFactory);
            } else {
                connector = new NetworkTrafficServerConnector(server,new HttpConnectionFactory(httpConfiguration));
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
