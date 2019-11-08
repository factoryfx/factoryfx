package io.github.factoryfx.jetty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  usage example.
 *
 *  <pre>{@code
 *       public class SimpleHttpServer extends SimpleFactoryBase<Server, Void, SimpleHttpServer> {
 *
 *           public final FactoryAttribute<Server, JettyServerFactory<SimpleHttpServer>> server = new FactoryAttribute<>(JettyServerFactory.class);
 *
 *           {@literal @}Override
 *           protected Server createImpl() {
 *               return server.instance();
 *           }
 *       }
 *
 *       builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<SimpleHttpServer>())
 *           .withHost("localhost").withPort(8005)
 *           .withResource(ctx.get(CustomResourceFactory.class)).build());
 *
 *  }</pre>
 */
public class JettyServerFactory<R extends FactoryBase<?,R>> extends FactoryBase<Server,R> {

    private static final Logger jerseyLogger1 = Logger.getLogger(org.glassfish.jersey.internal.inject.Providers.class.getName());
    private static final Logger jerseyLogger2 = Logger.getLogger(org.glassfish.jersey.internal.Errors.class.getName());
    static {
        jerseyLogger1.setLevel(Level.SEVERE); //another useless warning https://github.com/jersey/jersey/issues/3700
        jerseyLogger2.setLevel(Level.SEVERE);//warning about generic parameters, works fine and no fix available so the warnings are just useless
    }

    public final FactoryListAttribute<HttpServerConnector,HttpServerConnectorFactory<R>> connectors = new FactoryListAttribute<HttpServerConnector,HttpServerConnectorFactory<R>>().labelText("Connectors").userNotSelectable();
    public final FactoryAttribute<HandlerCollection,HandlerCollectionFactory<R>> handler = new FactoryAttribute<HandlerCollection,HandlerCollectionFactory<R>>().labelText("Handler collection");
    public final FactoryPolymorphicAttribute<ThreadPool> threadPool = new FactoryPolymorphicAttribute<ThreadPool>().labelText("Thread Pool").nullable();


    public JettyServerFactory(){
        configLifeCycle().setCreator(this::createJetty);
        configLifeCycle().setUpdater(this::update);
        configLifeCycle().setStarter(this::start);
        configLifeCycle().setDestroyer(this::stop);

        config().setDisplayTextProvider(() -> "Jetty http server");
    }

    //api for customizing JettyServer creation
    protected Server createJetty() {
        Server server;
        if (threadPool.get()==null){
            server = new Server();
        } else {
            server = new Server(threadPool.instance());
        }
        connectors.instances().forEach(httpServerConnector -> httpServerConnector.addToServer(server));

        handler.instance().setServer(server);
        server.setHandler(handler.instance());
        return server;
    }

    private void update(Server server){
        for (Connector connector : server.getConnectors()) {
            server.removeConnector(connector);
        }
        for (HttpServerConnector connector : connectors.instances()) {
            connector.addToServer(server);
        }
        if (server.getThreadPool() instanceof QueuedThreadPool && threadPool.get() instanceof ThreadPoolFactory){
            ((QueuedThreadPool) server.getThreadPool()).setMaxThreads(((ThreadPoolFactory)threadPool.get()).poolSize.get());
        }

    }

    /**
     * model navigation shortcut, only works with th e default setup form the builder
     * @param clazz resource clazz
     * @param <T>  resource type
     * @return resource
     */
    public final <T extends FactoryBase> T getResource(Class<T> clazz){
        return getDefaultJerseyServlet().resources.get(clazz);
    }

    /**
     * model navigation shortcut, only works with th e default setup form the builder
     * @param resource resource
     * @param <T> resource type
     */
    public final <T extends FactoryBase<?,?>> void setResource(FactoryBase<?,?> resource){
        JerseyServletFactory<R> jerseyServletFactory = getDefaultJerseyServlet();
        jerseyServletFactory.resources.removeIf(factoryBase -> factoryBase.getClass()==resource.getClass());
        jerseyServletFactory.resources.add(resource);
    }

    /**
     * model navigation shortcut, only works with th e default setup form the builder
     * @param clazz servlet class
     * @param <T> servlet type
     * @return servlet
     */
    @SuppressWarnings("unchecked")
    public final <T extends FactoryBase> T getServlet(Class<T> clazz){
        ServletContextHandlerFactory<R> servletContextHandler = getDefaultServletContextHandlerFactory();
        for (ServletAndPathFactory<R> servletAndPath : servletContextHandler.updatableRootServlet.get().servletAndPaths) {
            if (servletAndPath.servlet.get().getClass()==clazz){
                return (T)servletAndPath.servlet.get();
            }
        }
        return null;
    }

    public final <T extends FactoryBase> void clearResource(Class<T> resource){
        getDefaultJerseyServlet().resources.removeIf(factoryBase -> factoryBase.getClass()==resource);
    }

    @SuppressWarnings("unchecked")
    private JerseyServletFactory<R> getDefaultJerseyServlet() {
        ServletContextHandlerFactory<R> servletContextHandler = getDefaultServletContextHandlerFactory();
        ServletAndPathFactory<R> servletAndPathFactory = servletContextHandler.updatableRootServlet.get().servletAndPaths.get(0);
        return (JerseyServletFactory<R>) servletAndPathFactory.servlet.get();
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public ServletContextHandlerFactory<R> getDefaultServletContextHandlerFactory() {
        return (ServletContextHandlerFactory<R>)((handler.get().handlers.get(GzipHandlerFactory.class)).handler.get());
    }

    public void start(Server server) throws Error {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(Server server) {
        try {
            if (Thread.interrupted())
                throw new RuntimeException("Interrupted");
            server.setStopTimeout(1L);
            server.stop();
            //because stop call can be inside this one of jetty's threads, we need to clear the interrupt
            //to let the rest of the factory updates run. They might be interrupt-sensitive
            Thread.interrupted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
