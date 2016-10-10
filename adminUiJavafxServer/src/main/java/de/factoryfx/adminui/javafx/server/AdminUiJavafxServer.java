package de.factoryfx.adminui.javafx.server;

import java.util.Collection;
import java.util.function.Function;
import java.util.logging.LogManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
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

public class AdminUiJavafxServer {

    private final ApplicationServerResource factoryManagerResource;
    private final Server server;

    public AdminUiJavafxServer(ApplicationServerResource factoryManagerResource,
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

        LoggingFeature loggingFilter = new LoggingFeature(java.util.logging.Logger.getLogger(LoggingFeature.class.getName()));
        resourceConfig.registerInstances(loggingFilter);
        LogManager.getLogManager().reset();
        return resourceConfig;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk7Module());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return objectMapper;
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
