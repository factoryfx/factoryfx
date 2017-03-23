package de.factoryfx.server.rest.server;

import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    private final org.eclipse.jetty.server.Server server;

    public JettyServer(List<Function<Server,ServerConnector>> connectors, List<Object> resources) {
        server=new org.eclipse.jetty.server.Server();
        connectors.forEach(serverServerConnectorFunction -> server.addConnector(serverServerConnectorFunction.apply(server)));

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        resources.forEach(jerseyResource -> contextHandler.addServlet( new ServletHolder(new ServletContainer(jerseySetup(jerseyResource))), "/*"));

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

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{contextHandler, contextHandler});
        gzipHandler.setHandler(handlers);
        server.setHandler(gzipHandler);
    }

    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

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
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
