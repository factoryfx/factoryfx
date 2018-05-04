package de.factoryfx.jetty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    private final org.eclipse.jetty.server.Server server;
    private final Set<de.factoryfx.jetty.HttpServerConnectorCreator> currentConnectors = new HashSet<>();
    private final UpdateableServlet rootServlet;
    private boolean disposed = false;
    private final ObjectMapper objectMapper;
    private final LoggingFeature loggingFeature;
    private final Consumer<ResourceConfig> resourceConfigSetup;


    public JettyServer(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, List<Object> resources, List<Handler> additionalHandlers, ObjectMapper objectMapper, LoggingFeature loggingFeature, Consumer<ResourceConfig> resourceConfigSetup) {
        this.server=new org.eclipse.jetty.server.Server();
        this.objectMapper=objectMapper;
        this.loggingFeature=loggingFeature;
        this.currentConnectors.addAll(connectors);
        for (de.factoryfx.jetty.HttpServerConnectorCreator creator : currentConnectors) {
            creator.addToServer(server);
        }
        this.resourceConfigSetup = resourceConfigSetup;

        this.rootServlet = new UpdateableServlet(new ServletContainer(jerseySetup(resources)));
        ServletHolder holder = new ServletHolder(rootServlet);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( holder, "/*");
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);

        GzipHandler gzipHandler = new GzipHandler();
//            HashSet<String> mimeTypes = new HashSet<>();
//            mimeTypes.add("text/html");
//            mimeTypes.add("text/plain");
//            mimeTypes.add("text/css");
//            mimeTypes.add("application/x-javascript");
//            mimeTypes.add("application/json");
        gzipHandler.setMinGzipSize(0);

        gzipHandler.setHandler(contextHandler);

        HandlerCollection handlers = new HandlerList();
        additionalHandlers.forEach(handlers::addHandler);
        handlers.addHandler(gzipHandler);
        server.setHandler(handlers);
    }


    public JettyServer(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, List<Object> resources, ObjectMapper objectMapper, LoggingFeature loggingFeature) {
        this(connectors,resources,new ArrayList<>(),
                objectMapper!=null?objectMapper:ObjectMapperBuilder.buildNewObjectMapper(),
                loggingFeature!=null?loggingFeature:new org.glassfish.jersey.logging.LoggingFeature(new DelegatingLoggingFilterLogger()),new DefaultResourceConfigSetup());
    }

    public JettyServer(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, List<Object> resources) {
        this(connectors,resources,new ArrayList<>(),ObjectMapperBuilder.buildNewObjectMapper(),new org.glassfish.jersey.logging.LoggingFeature(new DelegatingLoggingFilterLogger()),new DefaultResourceConfigSetup());
    }

    private JettyServer(JettyServer priorServer) {
        this.rootServlet = priorServer.rootServlet;
        this.currentConnectors.addAll(priorServer.currentConnectors);
        this.server = priorServer.server;
        priorServer.disposed = true;
        this.objectMapper = priorServer.objectMapper;
        this.loggingFeature = priorServer.loggingFeature;
        this.resourceConfigSetup = priorServer.resourceConfigSetup;
    }

    private ResourceConfig jerseySetup(List<Object>  resource) {

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
        resource.forEach(resourceConfig::register);



        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        resourceConfig.register(provider);

        resourceConfigSetup.accept(resourceConfig);

        resourceConfig.registerInstances(loggingFeature);
        return resourceConfig;
    }


    public void start() throws Error {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (disposed)
            return;
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public JettyServer recreate(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, List<Object> resources) {
        JettyServer newServer = new JettyServer(this);
        newServer._recreate(connectors,resources);
        return newServer;
    }

    private void _recreate(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, List<Object> resources) {
        Set<de.factoryfx.jetty.HttpServerConnectorCreator> oldConnectors = new HashSet<>(currentConnectors);
        oldConnectors.removeAll(connectors);
        for (de.factoryfx.jetty.HttpServerConnectorCreator creator : oldConnectors) {
            creator.removeFromServer(server);
        }
        currentConnectors.clear();
        currentConnectors.addAll(connectors);
        for (de.factoryfx.jetty.HttpServerConnectorCreator currentConnector : currentConnectors) {
            currentConnector.addToServer(server);
        }
        rootServlet.update(new ServletContainer(jerseySetup(resources)));
    }

}
