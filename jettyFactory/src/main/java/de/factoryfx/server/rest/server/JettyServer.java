package de.factoryfx.server.rest.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.server.rest.server.soap.Soap11Provider;
import de.factoryfx.server.rest.server.soap.Soap12Provider;
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
    private final Set<HttpServerConnectorCreator> currentConnectors = new HashSet<>();
    private final UpdateableServlet rootServlet;
    private boolean disposed = false;
    private final ObjectMapper objectMapper;
    private final LoggingFeature loggingFeature;

    public JettyServer(List<HttpServerConnectorCreator> connectors, List<Object> resources, List<Handler> additionalHandlers, ObjectMapper objectMapper, LoggingFeature loggingFeature) {
        server=new org.eclipse.jetty.server.Server();
        this.objectMapper=objectMapper;
        this.loggingFeature=loggingFeature;
        currentConnectors.addAll(connectors);
        for (HttpServerConnectorCreator creator : currentConnectors) {
            creator.addToServer(server);
        }

        rootServlet = new UpdateableServlet(new ServletContainer(jerseySetup(resources)));
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

    public JettyServer(List<HttpServerConnectorCreator> connectors, List<Object> resources, ObjectMapper objectMapper, LoggingFeature loggingFeature) {
        this(connectors,resources,new ArrayList<>(),
                objectMapper!=null?objectMapper:ObjectMapperBuilder.buildNewObjectMapper(),
                loggingFeature!=null?loggingFeature:new org.glassfish.jersey.logging.LoggingFeature(new DelegatingLoggingFilterLogger()));
    }

    public JettyServer(List<HttpServerConnectorCreator> connectors, List<Object> resources) {
        this(connectors,resources,new ArrayList<>(),ObjectMapperBuilder.buildNewObjectMapper(),new org.glassfish.jersey.logging.LoggingFeature(new DelegatingLoggingFilterLogger()));
    }

    private JettyServer(JettyServer priorServer) {
        this.rootServlet = priorServer.rootServlet;
        this.currentConnectors.addAll(priorServer.currentConnectors);
        this.server = priorServer.server;
        priorServer.disposed = true;
        this.objectMapper = priorServer.objectMapper;
        this.loggingFeature = priorServer.loggingFeature;
    }

    private ResourceConfig jerseySetup(List<Object>  resource) {

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);// without we have 2 JacksonJaxbJsonProvider and wrong mapper
//        resourceConfig.register(resource);
        resource.forEach(resourceConfig::register);
        resourceConfig.register(new AllExceptionMapper());
        resourceConfig.register(Soap11Provider.class);
        resourceConfig.register(Soap12Provider.class);

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        resourceConfig.register(provider);

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


    public JettyServer recreate(List<HttpServerConnectorCreator> connectors, List<Object> resources) {
        JettyServer newServer = new JettyServer(this);
        newServer._recreate(connectors,resources);
        return newServer;
    }

    private void _recreate(List<HttpServerConnectorCreator> connectors, List<Object> resources) {
        Set<HttpServerConnectorCreator> oldConnectors = new HashSet<>(currentConnectors);
        oldConnectors.removeAll(connectors);
        for (HttpServerConnectorCreator creator : oldConnectors) {
            creator.removeFromServer(server);
        }
        currentConnectors.clear();
        currentConnectors.addAll(connectors);
        for (HttpServerConnectorCreator currentConnector : currentConnectors) {
            currentConnector.addToServer(server);
        }
        rootServlet.update(new ServletContainer(jerseySetup(resources)));
    }

}
