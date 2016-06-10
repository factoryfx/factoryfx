package de.factoryfx.development.angularjs.server;

import java.nio.file.Paths;
import java.util.UUID;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.development.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.development.angularjs.server.resourcehandler.FilesystemFileContentProvider;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.jettyserver.AllExceptionMapper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class WebGuiServer {
        private org.eclipse.jetty.server.Server server;
        private final Integer httpPort;
        private final String host;

        private ServerConnector connector;
        private final WebGuiResource webGuiResource;

        public WebGuiServer(Integer httpPort, String host, WebGuiResource webGuiResource) {
            super();
            this.httpPort = httpPort;
            this.host = host;
            this.webGuiResource =webGuiResource;
        }


        public void start() {
            System.setProperty("java.net.preferIPv4Stack", "true");//TODO optional?

            server = new org.eclipse.jetty.server.Server();

            connector = new NetworkTrafficServerConnector(server);
            connector.setPort(httpPort);
            connector.setHost(host);
            server.addConnector(connector);

            ConfigurableResourceHandler resourceHandler = new ConfigurableResourceHandler(new FilesystemFileContentProvider(Paths.get("./src/main/resources/webapp")), () -> UUID.randomUUID().toString());


            ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            contextHandler.addServlet( new ServletHolder(new ServletContainer(jerseySetup(webGuiResource))), "/applicationServer/*");

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
            handlers.setHandlers(new Handler[] { resourceHandler, contextHandler });
            gzipHandler.setHandler(handlers);
            server.setHandler(gzipHandler);

            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            try {
                server.setStopAtShutdown(true);
                server.stop();
                server.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(ObjectMapperBuilder.build().getObjectMapper());
        resourceConfig.register(provider);

        LoggingFeature loggingFilter = new LoggingFeature(java.util.logging.Logger.getLogger(LoggingFeature.class.getName()));
        resourceConfig.registerInstances(loggingFilter);
        return resourceConfig;
    }

}
