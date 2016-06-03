package de.factoryfx.server;

import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class ApplicationServer {

    private final ApplicationServerResource factoryManagerResource;
    private final Server server;
    private final ServerConnector connector;

    public ApplicationServer(ApplicationServerResource factoryManagerResource, ServerConnector connector) {
        this.factoryManagerResource = factoryManagerResource;
        this.connector = connector;

        server=new Server();

        server.setConnectors(new Connector[]{this.connector});

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( new ServletHolder(new ServletContainer(jerseySetup(factoryManagerResource))), "/applicationServer/*");

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {contextHandler, new DefaultHandler() });
        server.setHandler(handlers);
    }

    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

        ObjectMapper mapper = new ObjectMapper();

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        resourceConfig.register(provider);

        LoggingFeature loggingFilter = new LoggingFeature(java.util.logging.Logger.getLogger(LoggingFeature.class.getName()));
        resourceConfig.registerInstances(loggingFilter);
        LogManager.getLogManager().reset();
        return resourceConfig;
    }

    public void start() throws Error {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
