package de.factoryfx.server.rest.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.server.rest.server.soap.Soap11Provider;
import de.factoryfx.server.rest.server.soap.Soap12Provider;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    private final org.eclipse.jetty.server.Server server;
    private final Set<HttpServerConnectorCreator> currentConnectors = new HashSet<>();
    private final UpdateableServlet rootServlet;
    private boolean disposed = false;

    public JettyServer(List<HttpServerConnectorCreator> connectors, List<Object> resources) {
        server=new org.eclipse.jetty.server.Server();
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
        server.setHandler(gzipHandler);
    }

    private ResourceConfig jerseySetup(List<Object>  resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
//        resourceConfig.register(resource);
        resource.forEach(resourceConfig::register);
        resourceConfig.register(new AllExceptionMapper());
        resourceConfig.register(Soap11Provider.class);
        resourceConfig.register(Soap12Provider.class);

        ObjectMapper mapper = createObjectMapper();

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        resourceConfig.register(provider);

        org.glassfish.jersey.logging.LoggingFeature loggingFilter = new org.glassfish.jersey.logging.LoggingFeature(java.util.logging.Logger.getLogger(JettyServerFactory.class.getName()));
        resourceConfig.registerInstances(loggingFilter);
        return resourceConfig;
    }

    private ObjectMapper createObjectMapper() {
        return ObjectMapperBuilder.buildNewObjectMapper();
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

    private JettyServer(JettyServer priorServer) {
        this.rootServlet = priorServer.rootServlet;
        this.currentConnectors.addAll(priorServer.currentConnectors);
        this.server = priorServer.server;
        priorServer.disposed = true;
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
