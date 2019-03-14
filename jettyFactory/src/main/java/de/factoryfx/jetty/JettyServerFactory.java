package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  usage example.
 *
 *  <pre>{@code
        public class SimpleHttpServer extends SimpleFactoryBase<Server, Void, SimpleHttpServer> {
            @SuppressWarnings("unchecked")
            public final FactoryReferenceAttribute<Server, JettyServerFactory<Void, SimpleHttpServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

            @Override
            public Server createImpl() {
                return server.instance();
            }
        }

        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new JettyServerFactory<Void,SimpleHttpServer>())
            .withHost("localhost").withPort(8005)
            .withResource(ctx.get(CustomResourceFactory.class)).build());

   }</pre>
 */
public class JettyServerFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<Server,V,R> {

    private static final Logger jerseyLogger1 = Logger.getLogger(org.glassfish.jersey.internal.inject.Providers.class.getName());
    private static final Logger jerseyLogger2 = Logger.getLogger(org.glassfish.jersey.internal.Errors.class.getName());
    static {
        jerseyLogger1.setLevel(Level.SEVERE); //another useless warning https://github.com/jersey/jersey/issues/3700
        jerseyLogger2.setLevel(Level.SEVERE);//warning about generic parameters, works fine and no fix available so the warnings are just useless
    }

    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<HttpServerConnector,HttpServerConnectorFactory<V,R>> connectors =
            FactoryReferenceListAttribute.create( new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("Connectors").userNotSelectable());


    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<HandlerCollection,HandlerCollectionFactory<V,R>> handler = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(HandlerCollectionFactory.class).labelText("Handler collection"));


    public JettyServerFactory(){
        configLifeCycle().setCreator(this::createJetty);
        configLifeCycle().setUpdater(this::update);
        configLifeCycle().setStarter(this::start);
        configLifeCycle().setDestroyer(this::stop);

        config().setDisplayTextProvider(() -> "Microservice REST server");
    }

    //api for customizing JettyServer creation
    protected Server createJetty() {
        Server server = new Server();
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
    public final <T extends FactoryBase<?,V,R>> void setResource(T resource){
        JerseyServletFactory<V, R> jerseyServletFactory = getDefaultJerseyServlet();
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
        ServletContextHandlerFactory<V, R> servletContextHandler = (ServletContextHandlerFactory<V, R>) handler.get().handlers.get(GzipHandlerFactory.class).handler.get();
        for (ServletAndPathFactory<V, R> servletAndPath : servletContextHandler.updatableRootServlet.get().servletAndPaths) {
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
    private JerseyServletFactory<V, R> getDefaultJerseyServlet() {
        ServletContextHandlerFactory<V, R> servletContextHandler = (ServletContextHandlerFactory<V, R>) handler.get().handlers.get(GzipHandlerFactory.class).handler.get();
        ServletAndPathFactory<V, R> servletAndPathFactory = servletContextHandler.updatableRootServlet.get().servletAndPaths.get(0);
        return (JerseyServletFactory<V, R>) servletAndPathFactory.servlet.get();
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
