package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.jetty.ssl.SslContextFactoryFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.zip.Deflater;

/**
 * The builder builds the factory structure for a jetty server not the jetty liveobject<br>
 * The factory structure matches the jetty internal architecture and the JettyServerBuilder creates a default configuration for that.
 * <br>
 * jetty builder<br>
 * {@literal    =>jetty factories}<br>
 * {@literal           =>real jetty server}<br>
 *
 * @param <R> server root
 */
public class JettyServerBuilder<R extends FactoryBase<?,R>> {
    public JettyServerFactory<R> jettyServerFactory;
    private final JerseyServletFactory<R> defaultJerseyServlet;
    private final UpdateableServletFactory<R> updateableServletFactory;
    private final ServletAndPathFactory<R> defaultJerseyServletAndPathFactory;
    private final ThreadPoolFactory<R> threadPoolFactory;

    public JettyServerBuilder(){
        this.jettyServerFactory=new JettyServerFactory<>();

        this.threadPoolFactory=new ThreadPoolFactory<>();
        this.threadPoolFactory.poolSize.set(200);
        this.jettyServerFactory.threadPool.set(threadPoolFactory);

        HttpServerConnectorFactory<R> serverConnectorFactory = new HttpServerConnectorFactory<>();
        serverConnectorFactory.host.set("localhost");
        jettyServerFactory.connectors.add(serverConnectorFactory);


        HandlerCollectionFactory<R> handlerCollection = new HandlerCollectionFactory<>();
        jettyServerFactory.handler.set(handlerCollection);

        GzipHandlerFactory<R> gzipHandler = new GzipHandlerFactory<>();
        gzipHandler.minGzipSize.set(0);
        gzipHandler.compressionLevel.set(Deflater.DEFAULT_COMPRESSION);
        gzipHandler.deflaterPoolCapacity.set(-1);
        gzipHandler.dispatcherTypes.add(DispatcherType.REQUEST);
        gzipHandler.inflateBufferSize.set(-1);
        gzipHandler.syncFlush.set(false);
        handlerCollection.handlers.add(gzipHandler);

        ServletContextHandlerFactory<R> servletContextHandlerFactory = new ServletContextHandlerFactory<>();
        handlerCollection.handlers.add(servletContextHandlerFactory);

        updateableServletFactory = new UpdateableServletFactory<>();
        servletContextHandlerFactory.updatableRootServlet.set(updateableServletFactory);

        defaultJerseyServlet = new JerseyServletFactory<>();
        defaultJerseyServlet.objectMapper.set(new DefaultObjectMapperFactory<>());
        defaultJerseyServlet.restLogging.set(new Slf4LoggingFeatureFactory<>());

        defaultJerseyServletAndPathFactory = new ServletAndPathFactory<>();
        defaultJerseyServletAndPathFactory.pathSpec.set("/*");
        defaultJerseyServletAndPathFactory.servlet.set(defaultJerseyServlet);
        updateableServletFactory.servletAndPaths.add(defaultJerseyServletAndPathFactory);
    }

    public <S extends JettyServerFactory<R>> S buildTo(S derivedJettyServerFactory){
        derivedJettyServerFactory.threadPool.set(this.jettyServerFactory.threadPool.get());
        derivedJettyServerFactory.handler.set(this.jettyServerFactory.handler.get());
        derivedJettyServerFactory.connectors.set(this.jettyServerFactory.connectors);
        return derivedJettyServerFactory;
    }

    /** if jettyServerFactory is the root use {@link #buildTo}
     *
     * @return JettyServerFactory
     */
    public JettyServerFactory<R> build(){
        return jettyServerFactory;
    }

    public JettyServerBuilder<R> withPort(int port){
        jettyServerFactory.connectors.get(0).port.set(port);
        return this;
    }

    public JettyServerBuilder<R> withHost(String host){
        jettyServerFactory.connectors.get(0).host.set(host);
        return this;
    }

    public JettyServerBuilder<R> withHostWildcard(){
        return withHost("0.0.0.0");
    }

    /**
     *  jetty for example picks a free port
     * read port: ((ServerConnector)server.getConnectors()[0]).getLocalPort().
     * @return builder
     */
    public JettyServerBuilder<R> withRandomPort(){
        jettyServerFactory.connectors.get(0).port.set(0);
        return this;
    }

    public JettyServerBuilder<R> withResource(FactoryBase<?,R> resource){
        checkDefaultJerseyServletIsUsed();
        defaultJerseyServlet.resources.add(resource);
        return this;
    }

    public JettyServerBuilder<R> withAdditionalConnector(HttpServerConnectorFactory<R> httpServerConnectorFactory){
        jettyServerFactory.connectors.add(httpServerConnectorFactory);
        return this;
    }

    /**
     * set the base pathSpec for resources default is: /*
     *
     * @param pathSpec servlet spec path
     * @return builder
     */
    public JettyServerBuilder<R> withResourcePathSpec(String pathSpec){
        defaultJerseyServletAndPathFactory.pathSpec.set(pathSpec);
        return this;
    }

    public JettyServerBuilder<R> withJaxrsComponent(FactoryBase<Object,R> jaxrsComponentFactory){
        checkDefaultJerseyServletIsUsed();
        defaultJerseyServlet.additionalJaxrsComponents.add(jaxrsComponentFactory);
        return this;
    }

    public JettyServerBuilder<R> removeDefaultJerseyServlet(){
        if (!defaultJerseyServlet.resources.isEmpty()){
            throw new IllegalStateException("DefaultJerseyServlet cannot be removed if it contains resources");
        }
        updateableServletFactory.servletAndPaths.remove(defaultJerseyServletAndPathFactory);
        return this;
    }

    public JettyServerBuilder<R> withServlet(String pathSpec, FactoryBase<? extends Servlet,R> servlet){
        ServletAndPathFactory<R> servletAndPathFactory = new ServletAndPathFactory<>();
        servletAndPathFactory.pathSpec.set(pathSpec);
        servletAndPathFactory.servlet.set(servlet);
        updateableServletFactory.servletAndPaths.add(servletAndPathFactory);
        return this;
    }

    public JettyServerBuilder<R> withSsl(SslContextFactoryFactory<R> ssl) {
        jettyServerFactory.connectors.get(0).ssl.set(ssl);
        return this;
    }

    /**
     * default is 200
     * @param size jetty thread pool size
     */
    public void withThreadPoolSize(int size){
        this.threadPoolFactory.poolSize.set(size);
    }

    public JettyServerBuilder<R> withDefaultJerseyObjectMapper(FactoryBase<? extends ObjectMapper, R> objectMapperFactory){
        checkDefaultJerseyServletIsUsed();
        defaultJerseyServlet.objectMapper.set(objectMapperFactory);
        return this;
    }

    /**
     * shortcut <pre>withExceptionMapper(AttributelessFactory.create(TestExceptionMapper.class))</pre>
     * @param exceptionMapper exceptionMapper Factory
     * @return builder
     */
    public JettyServerBuilder<R> withExceptionMapper(FactoryBase<ExceptionMapper<Throwable>,R> exceptionMapper) {
        checkDefaultJerseyServletIsUsed();
        defaultJerseyServlet.exceptionMapper.set(exceptionMapper);
        return this;
    }

    private void checkDefaultJerseyServletIsUsed() {
        if (!updateableServletFactory.servletAndPaths.contains(defaultJerseyServletAndPathFactory)) {
            throw new IllegalStateException("Can't add resource because DefaultJerseyServlet is removed ");
        }
    }
}
