package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.jetty.ssl.SslContextFactoryFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.zip.Deflater;

public class JettyServerBuilder<V,R extends FactoryBase<?,V,R>,S extends JettyServerFactory<V, R>> {
    public S jettyServerFactory;
    private final JerseyServletFactory<V, R> defaultJerseyServlet;
    private final UpdateableServletFactory<V, R> updateableServletFactory;
    private final ServletAndPathFactory<V, R> defaultJerseyServletAndPathFactory;

    public JettyServerBuilder(S jettyServerFactory){
        this.jettyServerFactory=jettyServerFactory;

        HttpServerConnectorManagerFactory<V, R> connectorManager = new HttpServerConnectorManagerFactory<>();
        jettyServerFactory.connectorManager.set(connectorManager);

        HttpServerConnectorFactory<V, R> serverConnectorFactory = new HttpServerConnectorFactory<>();
        serverConnectorFactory.host.set("localhost");
        connectorManager.connectors.add(serverConnectorFactory);


        HandlerCollectionFactory<V, R> handlerCollection = new HandlerCollectionFactory<>();
        jettyServerFactory.handler.set(handlerCollection);

        GzipHandlerFactory<V, R> gzipHandler = new GzipHandlerFactory<>();
        gzipHandler.minGzipSize.set(0);
        gzipHandler.compressionLevel.set(Deflater.DEFAULT_COMPRESSION);
        gzipHandler.deflaterPoolCapacity.set(-1);
        gzipHandler.dispatcherTypes.add(DispatcherType.REQUEST);
        gzipHandler.inflateBufferSize.set(-1);
        gzipHandler.syncFlush.set(false);
        handlerCollection.handlers.add(gzipHandler);

        ServletContextHandlerFactory<V, R> servletContextHandlerFactory = new ServletContextHandlerFactory<>();
        gzipHandler.handler.set(servletContextHandlerFactory);

        updateableServletFactory = new UpdateableServletFactory<>();
        servletContextHandlerFactory.updatableRootServlet.set(updateableServletFactory);

        defaultJerseyServlet = new JerseyServletFactory<>();
        defaultJerseyServlet.objectMapper.set(new DefaultObjectMapperFactory<>());
        defaultJerseyServlet.restLogging.set(new Slf4LoggingFeatureFactory<>());
        defaultJerseyServlet.additionalJaxrsComponents.set(new ArrayList<>());

        defaultJerseyServletAndPathFactory = new ServletAndPathFactory<>();
        defaultJerseyServletAndPathFactory.pathSpec.set("/*");
        defaultJerseyServletAndPathFactory.servlet.set(defaultJerseyServlet);
        updateableServletFactory.servletAndPaths.add(defaultJerseyServletAndPathFactory);
    }

//    public JettyServerBuilder(){
//        this(new JettyServerFactory());
//    }


    public S build(){
        return jettyServerFactory;
    }

    @SuppressWarnings("unchecked")
    public <S extends JettyServerFactory<V, R>> S build(Class<S> clazz){
        return (S)jettyServerFactory;
    }

    public JettyServerBuilder<V,R,S> widthPort(int port){
        jettyServerFactory.connectorManager.get().connectors.get(0).port.set(port);
        return this;
    }

    public JettyServerBuilder<V,R,S> withHost(String host){
        jettyServerFactory.connectorManager.get().connectors.get(0).host.set(host);
        return this;
    }

    public JettyServerBuilder<V,R,S> withHostWildcard(){
        return withHost("0.0.0.0");
    }

    public JettyServerBuilder<V,R,S> withResource(FactoryBase<?,V,R> resource){
        defaultJerseyServlet.resources.add(resource);
        return this;
    }

    public JettyServerBuilder<V,R,S> withJaxrsComponent(Object jaxrsComponent){
        defaultJerseyServlet.additionalJaxrsComponents.get().add(jaxrsComponent);
        return this;
    }

    public JettyServerBuilder<V,R,S> removeDefaultJerseyServlet(){
        updateableServletFactory.servletAndPaths.remove(defaultJerseyServletAndPathFactory);
        return this;
    }

    public JettyServerBuilder<V,R,S> withServlet(String pathSpec, FactoryBase<Servlet,V,R> servlet){
        ServletAndPathFactory<V, R> servletAndPathFactory = new ServletAndPathFactory<>();
        servletAndPathFactory.pathSpec.set(pathSpec);
        servletAndPathFactory.servlet.set(servlet);
        updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        return this;
    }


    public JettyServerBuilder<V, R,S> witdhSsl(SslContextFactoryFactory<V, R> ssl) {
        jettyServerFactory.connectorManager.get().connectors.get(0).ssl.set(ssl);
        return this;
    }
}
