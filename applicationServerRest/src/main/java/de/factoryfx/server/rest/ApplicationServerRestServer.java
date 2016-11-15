package de.factoryfx.server.rest;

import java.util.Collection;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.server.rest.server.AllExceptionMapper;
import de.factoryfx.server.rest.server.DelegatingLoggingFilterLogger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class ApplicationServerRestServer {

    private final ApplicationServerResource factoryManagerResource;
    private final Server server;

    public ApplicationServerRestServer(ApplicationServerResource factoryManagerResource,
                                       Function<Server,Collection<ServerConnector>> connectorFactory, String contextPath) {
        this.factoryManagerResource = factoryManagerResource;
        server=new Server();
        Collection<ServerConnector> connectors = connectorFactory.apply(server);
        server.setConnectors(connectors.toArray(new ServerConnector[connectors.size()]));

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( new ServletHolder(new ServletContainer(jerseySetup(factoryManagerResource))), contextPath);

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);

        HandlerCollection handlers = new HandlerCollection();
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(contextHandler);
        handlers.setHandlers(new Handler[]{gzipHandler, new DefaultHandler()});

        server.setHandler(handlers);
    }

    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

        ObjectMapper mapper = createObjectMapper();

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        resourceConfig.register(provider);

        LoggingFeature loggingFilter = new LoggingFeature(new DelegatingLoggingFilterLogger());
        resourceConfig.registerInstances(loggingFilter);
        return resourceConfig;
    }

    private ObjectMapper createObjectMapper() {
        return ObjectMapperBuilder.buildNew().getObjectMapper();
    }

    public void start() throws Error {
        try {
            server.start();
        } catch (RuntimeException | Error re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (RuntimeException | Error re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
