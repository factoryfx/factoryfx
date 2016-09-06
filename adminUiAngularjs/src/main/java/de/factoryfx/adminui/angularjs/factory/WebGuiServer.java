package de.factoryfx.adminui.angularjs.factory;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.adminui.angularjs.server.AuthorizationRequestFilter;
import de.factoryfx.adminui.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.jettyserver.AllExceptionMapper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.LoggerFactory;

public class WebGuiServer<V> implements LiveObject<V> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebGuiServer.class);

    private org.eclipse.jetty.server.Server server;
    private final Integer httpPort;
    private final String host;

    private ServerConnector connector;
    private final WebGuiResource webGuiResource;
    private final ConfigurableResourceHandler resourceHandler;
    private final int sessionTimeoutS;

    public WebGuiServer(Integer httpPort, String host, int sessionTimeoutS, WebGuiResource webGuiResource, ConfigurableResourceHandler resourceHandler) {
        super();
        this.httpPort = httpPort;
        this.host = host;
        this.webGuiResource = webGuiResource;
        this.resourceHandler =resourceHandler;
        this.sessionTimeoutS = sessionTimeoutS;
    }


    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new AuthorizationRequestFilter());
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        ObjectMapper objectMapper = getObjectMapper();

        provider.setMapper(objectMapper);
        resourceConfig.register(provider);

        //workaround don't use java logging
        LoggingFeature loggingFilter = new LoggingFeature(new Logger(null, null) {
            @Override
            public void log(Level level, String msg) {
                logger.info(msg);
            }

            @Override
            public boolean isLoggable(Level level) {
                return true;
            }

        });
        resourceConfig.registerInstances(loggingFilter);
        return resourceConfig;
    }

    ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = ObjectMapperBuilder.buildNew().getObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new ToStringSerializer());
        module.addSerializer(Long.class, new ToStringSerializer());
        objectMapper.registerModule(module);

        //Disable JsonIdentityInfo
        JacksonAnnotationIntrospector ignoreJsonTypeInfoIntrospector = new JacksonAnnotationIntrospector() {
            @Override
            public ObjectIdInfo findObjectIdInfo(Annotated ann) {
                return null;
            }
        };
        objectMapper.setAnnotationIntrospector(ignoreJsonTypeInfoIntrospector);

        return objectMapper;
    }

    @Override
    public void start() {
        System.setProperty("java.net.preferIPv4Stack", "true");//TODO optional?

        server = new org.eclipse.jetty.server.Server();
        HashSessionIdManager sessionIdManager = new HashSessionIdManager();
        server.setSessionIdManager(sessionIdManager);

        connector = new NetworkTrafficServerConnector(server);
        connector.setPort(httpPort);
        connector.setHost(host);
        server.addConnector(connector);


        ServletContextHandler contextHandler = new ServletContextHandler();
        SessionHandler sessionHandler = new SessionHandler();
        HashSessionManager sessionManager = new HashSessionManager();
        sessionManager.setMaxInactiveInterval(sessionTimeoutS);
        sessionManager.setSessionCookie(sessionManager.getSessionCookie()+httpPort); //avoid session mixup for 2 server runnning as localhost
        sessionHandler.setSessionManager(sessionManager);


        contextHandler.setSessionHandler(sessionHandler);
        contextHandler.addServlet(new ServletHolder(new ServletContainer(jerseySetup(webGuiResource))), "/applicationServer/*");

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
//            gzipHandler.setMimeTypes(mimeTypes);

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{resourceHandler, contextHandler});
        gzipHandler.setHandler(handlers);
        server.setHandler(gzipHandler);

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            server.setStopAtShutdown(true);
            server.stop();
            server.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(V visitor) {

    }
}
