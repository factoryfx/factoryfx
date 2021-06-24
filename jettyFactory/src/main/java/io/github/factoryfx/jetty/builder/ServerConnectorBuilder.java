package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.HttpConfigurationFactory;
import io.github.factoryfx.jetty.HttpServerConnectorFactory;
import io.github.factoryfx.jetty.ssl.SslContextFactoryFactory;

/**
 * builder for jetty server connector.
 * a jetty server can have multiple connector e.g. one with ssl enabled and another one without ssl.
 * @param <R> rootFactory
 */
public class ServerConnectorBuilder<R extends FactoryBase<?,R>> {
    String host="localhost";
    int port=8080;
    SslContextFactoryFactory<R> ssl;
    HttpConfigurationFactory<R> httpConfiguration;
    boolean useHttp2=false;

    private final FactoryTemplateId<HttpServerConnectorFactory<R>> connectorTemplateId;

    public ServerConnectorBuilder(FactoryTemplateId<HttpServerConnectorFactory<R>> connectorTemplateId) {
        this.connectorTemplateId = connectorTemplateId;
    }

    /**
     * set port
     * @param port port
     * @return builder
     */
    public ServerConnectorBuilder<R> withPort(int port){
        this.port=port;
        return this;
    }

    /**
     * set host default is localhost
     * @param host host
     * @return builder
     */
    public ServerConnectorBuilder<R> withHost(String host){
        this.host=host;
        return this;
    }

    /**
     * set host to 0.0.0.0 , Mostly used to make a connector accessible from the outside.
     * @return builder
     */
    public ServerConnectorBuilder<R> withHostWildcard(){
        return withHost("0.0.0.0");
    }

    /**
     * configure ssl
     * @param ssl ssl factory
     * @return builder
     */
    public ServerConnectorBuilder<R> withSsl(SslContextFactoryFactory<R> ssl) {
        this.ssl=ssl;
        return this;
    }

    /**
     *  jetty for example picks a free port
     * read port: ((ServerConnector)server.getConnectors()[0]).getLocalPort().
     * @return builder
     */
    public ServerConnectorBuilder<R> withRandomPort(){
        port=0;
        return this;
    }

    public ServerConnectorBuilder<R> withHttpConfiguration(HttpConfigurationFactory<R> httpConfiguration){
        this.httpConfiguration=httpConfiguration;
        return this;
    }

    void build(FactoryTreeBuilder<?,R> builder) {
        builder.removeFactory(connectorTemplateId);
        builder.addFactory(connectorTemplateId, Scope.SINGLETON, (ctx)->{
            HttpServerConnectorFactory<R> serverConnectorFactory = new HttpServerConnectorFactory<>();
            serverConnectorFactory.host.set(host);
            serverConnectorFactory.port.set(port);
            serverConnectorFactory.useHttp2.set(useHttp2);

            if (ssl!=null) {
                serverConnectorFactory.ssl.set(ssl);
            }
            if (httpConfiguration!=null) {
                serverConnectorFactory.httpConfiguration.set(httpConfiguration);
            }

            return serverConnectorFactory;
        });


    }

    FactoryTemplateId<HttpServerConnectorFactory<R>> getTemplateId() {
        return this.connectorTemplateId;
    }

    /**
     * enables http 2
     * @return builder
     */
    public ServerConnectorBuilder<R> withHttp2() {
        useHttp2=true;
        return this;
    }
}
