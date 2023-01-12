package io.github.factoryfx.jetty.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.RequestLog;
import org.glassfish.jersey.logging.LoggingFeature;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryContext;
import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.NestedBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.GzipHandlerFactory;
import io.github.factoryfx.jetty.HandlerCollectionFactory;
import io.github.factoryfx.jetty.HttpConfigurationFactory;
import io.github.factoryfx.jetty.HttpServerConnectorFactory;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.jetty.ServletAndPathFactory;
import io.github.factoryfx.jetty.ServletContextHandlerFactory;
import io.github.factoryfx.jetty.ServletFilterAndPathFactory;
import io.github.factoryfx.jetty.Slf4jRequestLogFactory;
import io.github.factoryfx.jetty.ThreadPoolFactory;
import io.github.factoryfx.jetty.UpdateableServletFactory;
import io.github.factoryfx.jetty.ssl.ServerSslContextFactoryFactory;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * The builder builds the factory structure for a jetty server not the jetty liveobject<br>
 * The factory structure matches the jetty internal architecture and the JettyServerBuilder creates a default configuration for that.
 * <br>
 * {@code jetty builder==>jetty factories=>real jetty server}<br>
 *
 * @see <a href="https://dzone.com/refcardz/jetty?chapter=2">https://dzone.com/refcardz/jetty?chapter=2</a>
 * @param <R> server root
 */
public class JettyServerBuilder<R extends FactoryBase<?,R>, JR extends JettyServerFactory<R>> implements NestedBuilder<R> {


    private final List<ServletBuilder<R>> additionalServletBuilders = new ArrayList<>();
    private final List<ServletFilterBuilder<R>> servletFilterBuilders = new ArrayList<>();
    private FactoryBase<Handler, R> firstHandler;
    private int threadPoolSize=200;
    private boolean enabledRequestLog=true;

    private final FactoryTemplateId<JR> rootTemplateId;
    private ServerConnectorBuilder<R> defaultServerConnector;
    private List<ServerConnectorBuilder<R>> serverConnectorBuilders =new ArrayList<>();
    private ResourceBuilder<R> resourceBuilder;
    private List<ResourceBuilder<R>> resourceBuilders =new ArrayList<>();
    private Consumer<JR> additionalConfiguration = jr-> {};

    private final FactoryTemplateId<ThreadPoolFactory<R>> threadPoolFactoryTemplateId;
    private final FactoryTemplateId<FactoryBase<RequestLog,R>> requestLogerTemplateId;
    private final FactoryTemplateId<HandlerCollectionFactory<R>> handlerCollectionFactoryTemplateId;
    private final FactoryTemplateId<GzipHandlerFactory<R>> gzipHandlerFactoryTemplateId;
    private final FactoryTemplateId<ServletContextHandlerFactory<R>> servletContextHandlerFactoryTemplateId;
    private final FactoryTemplateId<UpdateableServletFactory<R>> updateableServletFactoryTemplateId;
    private final FactoryTemplateId<HttpServerConnectorFactory<R>> defaultServerConnectorTemplateId;

    private Consumer<GzipHandlerFactory<R>> gzipHandlerCustomizer=(gzipHandler)->{};

    private final Supplier<JR> jettyRootCreator;

    public JettyServerBuilder(FactoryTemplateId<JR> rootTemplateId, Supplier<JR> jettyRootCreator){
        this.rootTemplateId=rootTemplateId;
        this.jettyRootCreator=jettyRootCreator;

        this.threadPoolFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, ThreadPoolFactory.class);
        this.requestLogerTemplateId=new FactoryTemplateId<>(rootTemplateId.name, Slf4jRequestLogFactory.class);
        this.handlerCollectionFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, HandlerCollectionFactory.class);
        this.gzipHandlerFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, GzipHandlerFactory.class);
        this.servletContextHandlerFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, ServletContextHandlerFactory.class);
        this.updateableServletFactoryTemplateId=new FactoryTemplateId<>(rootTemplateId.name, UpdateableServletFactory.class);
        this.defaultServerConnectorTemplateId=new FactoryTemplateId<>(rootTemplateId.name, HttpServerConnectorFactory.class);
    }

    /**
     * add a jetty connector, useful for jetty on multiple ports or support both http and ssl
     *
     * @param connectorBuilderSetup connectorBuilderSetup e.g. {@code withAdditionalConnector((connector)->connector.withPort(8080),"con1")}
     * @param name builder unique name, used in the builder to associate match the templates
     * @return builder
     */
    public JettyServerBuilder<R, JR> withAdditionalConnector(Consumer<ServerConnectorBuilder<R>> connectorBuilderSetup, FactoryTemplateName name){
        ServerConnectorBuilder<R> resourceBuilder = new ServerConnectorBuilder<>(new FactoryTemplateId<>(this.rootTemplateId.name+name.name, ServletAndPathFactory.class));
        connectorBuilderSetup.accept(resourceBuilder);
        serverConnectorBuilders.add(resourceBuilder);
        return this;
    }

    /**
     * add a jetty connector, useful for jetty on multiple ports or support both http and ssl
     *
     * @param connectorBuilderSetup connectorBuilderSetup e.g. {@code withAdditionalConnector((connector)->connector.withPort(8080),"con1")}
     * @param name builder unique name, used in the builder to associate match the templates
     * @return builder
     */
    public JettyServerBuilder<R, JR> setAdditionalConnector(Consumer<ServerConnectorBuilder<R>> connectorBuilderSetup, FactoryTemplateName name){
        serverConnectorBuilders.clear();
        ServerConnectorBuilder<R> resourceBuilder = new ServerConnectorBuilder<>(new FactoryTemplateId<>(this.rootTemplateId.name+name.name, ServletAndPathFactory.class));
        connectorBuilderSetup.accept(resourceBuilder);
        serverConnectorBuilders.add(resourceBuilder);
        return this;
    }

    /**
     * add a new jersey servlet with REST Resources
     * this can be used to support different ObjectMappers/ExceptionHandler
     *
     * @param resourceBuilderSetup resourceBuilderSetup
     * @param name builder unique name, used in the builder to associate match the templates
     * @return builder
     */
    public JettyServerBuilder<R, JR> withJersey(Consumer<ResourceBuilder<R>> resourceBuilderSetup, FactoryTemplateName name){
        ResourceBuilder<R> resourceBuilder = new ResourceBuilder<>(new FactoryTemplateId<>(this.rootTemplateId.name+name.name, ServletAndPathFactory.class));
        resourceBuilderSetup.accept(resourceBuilder);
        addResourceBuilder(resourceBuilder);
        return this;
    }

    /**
     * adds a servlet
     * @param servlet servlet
     * @param pathSpec pathSpec
     * @param name builder unique name, used in the builder to associate match the templates
     * @return builder
     */
    public JettyServerBuilder<R, JR> withServlet(FactoryBase<? extends Servlet, R> servlet, String pathSpec, FactoryTemplateName name){
        additionalServletBuilders.add(new ServletBuilder<>(new FactoryTemplateId<>(null,name.name),pathSpec,servlet));
        return this;
    }

    /**
     * adds a servlet
     * @param templateId templateId for the servlet
     * @param pathSpec pathSpec
     * @param servlet servlet
     * @return builder
     */
    public JettyServerBuilder<R, JR> withServlet(FactoryTemplateId<ServletAndPathFactory<R>> templateId, String pathSpec, FactoryBase<? extends Servlet, R> servlet){
        additionalServletBuilders.add(new ServletBuilder<>(templateId,pathSpec,servlet));
        return this;
    }

    /**
     * adds a servlet filter
     * @param filter filter
     * @param pathSpec pathSpec
     * @param name builder unique name, used in the builder to associate match the templates
     * @return builder
     */
    public JettyServerBuilder<R, JR> withServletFilter(FactoryBase<? extends Filter, R> filter, String pathSpec, FactoryTemplateName name){
        servletFilterBuilders.add(new ServletFilterBuilder<>(new FactoryTemplateId<>(null,name.name),pathSpec,filter));
        return this;
    }

    /**
     * adds a servlet filter
     * @param templateId templateId for the filter
     * @param pathSpec pathSpec
     * @param filter filter
     * @return builder
     */
    public JettyServerBuilder<R, JR> withServletFilter(FactoryTemplateId<ServletFilterAndPathFactory<R>> templateId, String pathSpec, FactoryBase<? extends Filter, R> filter){
        servletFilterBuilders.add(new ServletFilterBuilder<>(templateId,pathSpec,filter));
        return this;
    }

    /**
     * default is 200
     * @param size jetty thread pool size
     * @return builder
     */
    public JettyServerBuilder<R, JR> withThreadPoolSize(int size){
        this.threadPoolSize=size;
        return this;
    }

    /**
     * disable the jetty request log
     * @return builder
     */
    public JettyServerBuilder<R, JR> withDisabledRequestLog(){
        this.enabledRequestLog=false;
        return this;
    }

    /**
     * adds a handler ad the first HandlerCollection position (with means handler is executed before the default)
     * @param firstHandler creator
     * @return builder
     */
    public JettyServerBuilder<R, JR> withHandlerFirst(FactoryBase<Handler,R> firstHandler) {
        this.firstHandler=firstHandler;
        return this;
    }

    /**
     * customize the gzipHandler setup
     * @param gzipHandlerCustomizer customizer consumer
     * @return builder
     */
    public JettyServerBuilder<R, JR> withGzipHandlerCustomizer(Consumer<GzipHandlerFactory<R>> gzipHandlerCustomizer) {
        this.gzipHandlerCustomizer = gzipHandlerCustomizer;
        return this;
    }

    /**
     * customize the jetty server
     * @param config customizer consumer
     * @return builder
     */
    public JettyServerBuilder<R, JR> withSpecificConfiguration(Consumer<JR> config) {
        this.additionalConfiguration = config;
        return this;
    }

    private <F extends FactoryBase<?,R>> void addFactory(FactoryTreeBuilder<?,R> builder, FactoryTemplateId<F> templateId, Scope scope, Function<FactoryContext<R>, F> creator){
        builder.removeFactory(templateId);
        builder.addFactory(templateId,scope,creator);
    }

    /**
     * internal method
     * @param builder tree builder to add
     */
    @Override
    public void internal_build(FactoryTreeBuilder<?,R> builder){
        addFactory(builder,rootTemplateId, Scope.SINGLETON, (FactoryContext<R> ctx) ->{
            JR jettyServerFactory=jettyRootCreator.get();
            additionalConfiguration.accept(jettyServerFactory);
            jettyServerFactory.threadPool.set(ctx.get(threadPoolFactoryTemplateId));
            if (enabledRequestLog) {
                jettyServerFactory.requestLog.set(ctx.get(requestLogerTemplateId));
            }
            for (ServerConnectorBuilder<R> connectorBuilder : serverConnectorBuilders) {
                jettyServerFactory.connectors.add( ctx.get(connectorBuilder.getTemplateId()));
            }

            jettyServerFactory.handler.set(ctx.get(handlerCollectionFactoryTemplateId));
            return jettyServerFactory;
        });

        if (enabledRequestLog) {
            addFactory(builder,requestLogerTemplateId, Scope.SINGLETON, (ctx) -> {
                return new Slf4jRequestLogFactory<>();
            });
        }

        addFactory(builder,threadPoolFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            ThreadPoolFactory<R> threadPoolFactory=new ThreadPoolFactory<>();
            threadPoolFactory.poolSize.set(threadPoolSize);
            return threadPoolFactory;
        });

        for (ServerConnectorBuilder<R> connectorBuilder : serverConnectorBuilders) {
            connectorBuilder.build(builder);
        }

        addFactory(builder,handlerCollectionFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            HandlerCollectionFactory<R> handlerCollection = new HandlerCollectionFactory<>();
            if (firstHandler!=null) {
                handlerCollection.handlers.add(firstHandler);
            }
            handlerCollection.handlers.add(ctx.get(gzipHandlerFactoryTemplateId));
            return handlerCollection;
        });

        addFactory(builder,gzipHandlerFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            GzipHandlerFactory<R> gzipHandler = new GzipHandlerFactory<>();
            gzipHandler.minGzipSize.set(23);
            gzipHandler.dispatcherTypes.add(DispatcherType.REQUEST);
            gzipHandler.inflateBufferSize.set(-1);
            gzipHandler.syncFlush.set(false);
            gzipHandler.handler.set(ctx.get(servletContextHandlerFactoryTemplateId));
            gzipHandlerCustomizer.accept(gzipHandler);
            return gzipHandler;
        });

        addFactory(builder,servletContextHandlerFactoryTemplateId, Scope.SINGLETON, (ctx)->{
            ServletContextHandlerFactory<R> servletContextHandlerFactory = new ServletContextHandlerFactory<>();
            servletContextHandlerFactory.updatableRootServlet.set(ctx.get(updateableServletFactoryTemplateId));
            for (ServletFilterBuilder<R> servletFilterBuilder : servletFilterBuilders) {
                servletContextHandlerFactory.servletFilters.add(ctx.get(servletFilterBuilder.getTemplateId()));
            }
            return servletContextHandlerFactory;
        });

        addFactory(builder,updateableServletFactoryTemplateId, Scope.SINGLETON, (ctx)->{
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

        for (ServletFilterBuilder<R> filterBuilder : servletFilterBuilders) {
            filterBuilder.build(builder);
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
            serverConnectorBuilders.add(defaultServerConnector);
        }
        return this.defaultServerConnector;
    }

    /**
     * @see ServerConnectorBuilder#withPort(int)
     * @param port port
     * @return builder
     */
    public JettyServerBuilder<R, JR> withPort(int port){
        getDefaultServerConnector().withPort(port);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHost(String)
     * @param host host
     * @return builder
     */
    public JettyServerBuilder<R, JR> withHost(String host){
        getDefaultServerConnector().withHost(host);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHostWildcard()
     * @return builder
     */
    public JettyServerBuilder<R, JR> withHostWildcard(){
        getDefaultServerConnector().withHostWildcard();
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withSsl(ServerSslContextFactoryFactory)
     * @param ssl ssl
     * @return builder
     */
    public JettyServerBuilder<R, JR> withSsl(ServerSslContextFactoryFactory<R> ssl) {
        getDefaultServerConnector().withSsl(ssl);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHttpConfiguration(HttpConfigurationFactory)
     * @param httpConfiguration httpConfiguration
     * @return builder
     */
    public JettyServerBuilder<R, JR> withHttpConfiguration(HttpConfigurationFactory<R> httpConfiguration) {
        getDefaultServerConnector().withHttpConfiguration(httpConfiguration);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withRandomPort()
     * @return builder
     */
    public JettyServerBuilder<R, JR> withRandomPort(){
        getDefaultServerConnector().withRandomPort();
        return this;
    }


    private ResourceBuilder<R> getDefaultJersey(){
        if (this.resourceBuilder==null){
            this.resourceBuilder=new ResourceBuilder<>(new FactoryTemplateId<>(this.rootTemplateId.name+"DefaultResource", ServletAndPathFactory.class));
            addResourceBuilder(resourceBuilder);

        }
        return this.resourceBuilder;
    }

    /**
     * @see ResourceBuilder#withPathSpec(String)
     * @param pathSpec pathSpec
     * @return builder
     */
    public JettyServerBuilder<R, JR> withPathSpec(String pathSpec){
        getDefaultJersey().withPathSpec(pathSpec);
        return this;
    }

    /**
     * add a new Resource to the default jersey servlet
     * @see ResourceBuilder#withResource(FactoryBase)
     * @param resource resource
     * @return builder
     */
    public JettyServerBuilder<R, JR> withResource(FactoryBase<?,R> resource){
        getDefaultJersey().withResource(resource);
        return this;
    }

    /**
     * @see ResourceBuilder#withJaxrsComponent(FactoryBase)
     * @param jaxrsComponent jaxrsComponent
     * @return builder
     */
    public JettyServerBuilder<R, JR> withJaxrsComponent(FactoryBase<?,R> jaxrsComponent){
        getDefaultJersey().withJaxrsComponent(jaxrsComponent);
        return this;
    }

    /**
     * @see ResourceBuilder#withLoggingFeature(FactoryBase)
     * @param loggingFeature loggingFeature
     * @return builder
     */
    public JettyServerBuilder<R, JR> withLoggingFeature(FactoryBase<LoggingFeature,R> loggingFeature){
        getDefaultJersey().withLoggingFeature(loggingFeature);
        return this;
    }

    /**
     * @see ResourceBuilder#withObjectMapper(FactoryBase)
     * @param objectMapper objectMapper
     * @return builder
     */
    public JettyServerBuilder<R, JR> withObjectMapper(FactoryBase<ObjectMapper,R> objectMapper){
        getDefaultJersey().withObjectMapper(objectMapper);
        return this;
    }

    /**
     * @see ResourceBuilder#withExceptionMapper(FactoryBase)
     * @param exceptionMapper exceptionMapper
     * @return builder
     */
    public JettyServerBuilder<R, JR> withExceptionMapper(FactoryBase<ExceptionMapper<Throwable>,R> exceptionMapper) {
        getDefaultJersey().withExceptionMapper(exceptionMapper);
        return this;
    }

    /**
     * @see ResourceBuilder#withJerseyProperties(Map)
     * @param jerseyProperties jerseyProperties
     * @return builder
     */
    public JettyServerBuilder<R, JR> withJerseyProperties(Map<String,Object> jerseyProperties) {
        getDefaultJersey().withJerseyProperties(jerseyProperties);
        return this;
    }

    /**
     * @see ServerConnectorBuilder#withHttp2()
     * @return builder
     */
    public JettyServerBuilder<R, JR> withHttp2() {
        getDefaultServerConnector().withHttp2();
        return this;
    }

}
