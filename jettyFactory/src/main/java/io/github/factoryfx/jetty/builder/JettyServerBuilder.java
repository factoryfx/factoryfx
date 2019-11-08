package io.github.factoryfx.jetty.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.*;
import io.github.factoryfx.jetty.*;
import io.github.factoryfx.jetty.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.Deflater;

/**
 * The builder builds the factory structure for a jetty server not the jetty liveobject<br>
 * The factory structure matches the jetty internal architecture and the JettyServerBuilder creates a default configuration for that.
 * <br>
 * jetty builder==>jetty factories=>real jetty server<br>
 *
 * @see <a href="https://dzone.com/refcardz/jetty?chapter=2">https://dzone.com/refcardz/jetty?chapter=2</a>
 * @param <R> server root
 */
public class JettyServerBuilder<L,R extends FactoryBase<L,R>, JR extends JettyServerFactory<R>> implements NestedBuilder<L,R> {


    private final List<ServletBuilder<R>> additionalServletBuilders = new ArrayList<>();
    private FactoryBase<Handler, R> firstHandler;
    private int threadPoolSize=200;

    private final FactoryTemplateId<R,JR> rootTemplateId;
    private ServerConnectorBuilder<R> defaultServerConnector;
    private List<ResourceBuilder<R>> resourceBuilders =new ArrayList<>();
    private ResourceBuilder<R> resourceBuilder;

    private final FactoryTemplateId<R,ThreadPoolFactory<R>> threadPoolFactoryTemplateId;
    private final FactoryTemplateId<R,HandlerCollectionFactory<R>> handlerCollectionFactoryTemplateId;
    private final FactoryTemplateId<R,GzipHandlerFactory<R>> gzipHandlerFactoryTemplateId;
    private final FactoryTemplateId<R,ServletContextHandlerFactory<R>> servletContextHandlerFactoryTemplateId;
    private final FactoryTemplateId<R,UpdateableServletFactory<R>> updateableServletFactoryTemplateId;
    private final FactoryTemplateId<R,HttpServerConnectorFactory<R>> defaultServerConnectorTemplateId;

    private final Supplier<JR> jettyRootCreator;

    public JettyServerBuilder(FactoryTemplateId<R,JR> rootTemplateId, Supplier<JR> jettyRootCreator){
        this.rootTemplateId=rootTemplateId;
        this.jettyRootCreator=jettyRootCreator;

        this.threadPoolFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, ThreadPoolFactory.class);
        this.handlerCollectionFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, HandlerCollectionFactory.class);
        this.gzipHandlerFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, GzipHandlerFactory.class);
        this.servletContextHandlerFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, ServletContextHandlerFactory.class);
        this.updateableServletFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, UpdateableServletFactory.class);
        this.defaultServerConnectorTemplateId=new FactoryTemplateId<>(rootTemplateId.name, HttpServerConnectorFactory.class);
    }

    /**
     *
     *
     * add a new jersey servlet with REST Resources
     * this can be used to have different ObjectMappers/ExceptionHandler
     *
     * @param resourceBuilderSetup
     * @param name
     * @return
     */
    public JettyServerBuilder<L,R, JR> withJersey(Consumer<ResourceBuilder<R>> resourceBuilderSetup, String name){
        ResourceBuilder<R> resourceBuilder = new ResourceBuilder<R>(new FactoryTemplateId<>(this.rootTemplateId.name+name, ServletAndPathFactory.class));
        resourceBuilderSetup.accept(resourceBuilder);
        addResourceBuilder(resourceBuilder);
        return this;
    }

    /**
     * adds a servlet
     * @param templateId templateId
     * @param pathSpec pathSpec
     * @param servlet servlet
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withServlet(FactoryTemplateId<R,ServletAndPathFactory<R>> templateId, String pathSpec, FactoryBase<? extends Servlet, R> servlet){
        additionalServletBuilders.add(new ServletBuilder<>(templateId,pathSpec,servlet));
        return this;
    }

    /**
     * default is 200
     * @param size jetty thread pool size
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withThreadPoolSize(int size){
        this.threadPoolSize=size;
        return this;
    }

    /**
     * adds a handler ad the first HandlerCollection position (with means handler is executed before the default)
     * @param firstHandler creator
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withHandlerFirst(FactoryBase<Handler,R> firstHandler) {
        this.firstHandler=firstHandler;
        return this;
    }

    /**
     * internal method
     * @param builder tree builder to add
     */
    @Override
    public void internal_build(FactoryTreeBuilder<L,R> builder){
        builder.addFactory(rootTemplateId, Scope.SINGLETON, (FactoryContext<R> ctx) ->{
            JR jettyServerFactory=jettyRootCreator.get();
            FactoryBase<ThreadPool, ?> factoryBase = ctx.get(threadPoolFactoryTemplateId);
            jettyServerFactory.threadPool.set(factoryBase);
            if (this.defaultServerConnector!=null){
                HttpServerConnectorFactory<R> connector = ctx.get(this.defaultServerConnector.getTemplateId());
                jettyServerFactory.connectors.add(connector);
            }
            jettyServerFactory.handler.set(ctx.get(handlerCollectionFactoryTemplateId));
            return jettyServerFactory;
        });

        builder.addFactory(threadPoolFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            ThreadPoolFactory<R> threadPoolFactory=new ThreadPoolFactory<>();
            threadPoolFactory.poolSize.set(threadPoolSize);
            return threadPoolFactory;
        });

        if (this.defaultServerConnector!=null){
            this.defaultServerConnector.build(builder);
        }

        builder.addFactory(handlerCollectionFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            HandlerCollectionFactory<R> handlerCollection = new HandlerCollectionFactory<>();
            if (firstHandler!=null) {
                handlerCollection.handlers.add(firstHandler);
            }
            handlerCollection.handlers.add(ctx.get(gzipHandlerFactoryTemplateId));
            return handlerCollection;
        });

        builder.addFactory(gzipHandlerFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            GzipHandlerFactory<R> gzipHandler = new GzipHandlerFactory<>();
            gzipHandler.minGzipSize.set(0);
            gzipHandler.compressionLevel.set(Deflater.DEFAULT_COMPRESSION);
            gzipHandler.deflaterPoolCapacity.set(-1);
            gzipHandler.dispatcherTypes.add(DispatcherType.REQUEST);
            gzipHandler.inflateBufferSize.set(-1);
            gzipHandler.syncFlush.set(false);
            gzipHandler.handler.set(ctx.get(servletContextHandlerFactoryTemplateId));
            return gzipHandler;
        });

        builder.addFactory(servletContextHandlerFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            ServletContextHandlerFactory<R> servletContextHandlerFactory = new ServletContextHandlerFactory<>();
            servletContextHandlerFactory.updatableRootServlet.set(ctx.get(updateableServletFactoryTemplateId));
            return servletContextHandlerFactory;
        });

        builder.addFactory(updateableServletFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            UpdateableServletFactory<R> updateableServletFactory = new UpdateableServletFactory<>();

            for (ResourceBuilder<R> resourceBuilder : resourceBuilders) {
                updateableServletFactory.servletAndPaths.add(ctx.get(resourceBuilder.getServletAndPathFactoryTemplateId()));
            }

            for (ServletBuilder<R> servletBuilder : additionalServletBuilders) {
                updateableServletFactory.servletAndPaths.add(ctx.get(servletBuilder.getTemplateId()));
            }
            return updateableServletFactory;
        });

        for (ResourceBuilder<R> resourceBuilder : resourceBuilders) {
            resourceBuilder.build(builder);
        }

        for (ServletBuilder<R> servletBuilder : additionalServletBuilders) {
            servletBuilder.build(builder);
        }

    }

    private void addResourceBuilder(ResourceBuilder<R> resourceBuilder){
        for (ResourceBuilder<R> builder : resourceBuilders) {
            if (builder.match(resourceBuilder)){
                throw new IllegalStateException("can't add multiple jersey servlets with the same patchSpec");
            }
        }

        resourceBuilders.add(resourceBuilder);
    }

    private ServerConnectorBuilder<R> getDefaultServerConnector(){
        if (this.defaultServerConnector==null){
            this.defaultServerConnector=new ServerConnectorBuilder<>(defaultServerConnectorTemplateId);
        }
        return this.defaultServerConnector;
    }

    /**
     * @see ServerConnectorBuilder#withPort(int)
     * @param port port
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withPort(int port){
        getDefaultServerConnector().withPort(port);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHost(String)
     * @param host host
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withHost(String host){
        getDefaultServerConnector().withHost(host);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHostWildcard()
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withHostWildcard(){
        getDefaultServerConnector().withHostWildcard();
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withSsl(SslContextFactoryFactory)
     * @param ssl ssl
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withSsl(SslContextFactoryFactory<R> ssl) {
        getDefaultServerConnector().withSsl(ssl);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withRandomPort()
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withRandomPort(){
        getDefaultServerConnector().withRandomPort();
        return this;
    }


    private ResourceBuilder<R> getDefaultJersey(){
        if (this.resourceBuilder==null){
            this.resourceBuilder=new ResourceBuilder<R>(new FactoryTemplateId<>(this.rootTemplateId.name+"DefaultResource", ServletAndPathFactory.class));
            addResourceBuilder(resourceBuilder);

        }
        return this.resourceBuilder;
    }

    /**
     * @see ResourceBuilder#withPathSpec(String)
     * @param pathSpec pathSpec
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withPathSpec(String pathSpec){
        getDefaultJersey().withPathSpec(pathSpec);
        return this;
    }

    /**
     * add a new Resource to the default jersey servlet
     * @see ResourceBuilder#withResource(FactoryBase)
     * @param resource resource
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withResource(FactoryBase<?,R> resource){
        getDefaultJersey().withResource(resource);
        return this;
    }

    /**
     * @see ResourceBuilder#withJaxrsComponent(FactoryBase)
     * @param jaxrsComponent jaxrsComponent
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withJaxrsComponent(FactoryBase<?,R> jaxrsComponent){
        getDefaultJersey().withJaxrsComponent(jaxrsComponent);
        return this;
    }

    /**
     * @see ResourceBuilder#withLoggingFeature(FactoryBase)
     * @param loggingFeature loggingFeature
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withLoggingFeature(FactoryBase<LoggingFeature,R> loggingFeature){
        getDefaultJersey().withLoggingFeature(loggingFeature);
        return this;
    }

    /**
     * @see ResourceBuilder#withObjectMapper(FactoryBase)
     * @param objectMapper objectMapper
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withObjectMapper(FactoryBase<ObjectMapper,R> objectMapper){
        getDefaultJersey().withObjectMapper(objectMapper);
        return this;
    }

    /**
     * @see ResourceBuilder#withExceptionMapper(FactoryBase)
     * @param exceptionMapper exceptionMapper
     * @return builder
     */
    public JettyServerBuilder<L,R, JR> withExceptionMapper(FactoryBase<ExceptionMapper<Throwable>,R> exceptionMapper) {
        getDefaultJersey().withExceptionMapper(exceptionMapper);
        return this;
    }

}
