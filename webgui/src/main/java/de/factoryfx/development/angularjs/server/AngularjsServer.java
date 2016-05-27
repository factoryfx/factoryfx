package de.factoryfx.development.angularjs.server;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.UUID;

import de.factoryfx.development.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.development.angularjs.server.resourcehandler.FilesystemFileContentProvider;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

public class AngularjsServer {
        private org.eclipse.jetty.server.Server server;
        private final Integer httpPort;
        private final String host;

        private ServerConnector connector;

        public AngularjsServer(Integer httpPort, String host) {
            super();
            this.httpPort = httpPort;
            this.host = host;
        }


        public void start() {
            System.setProperty("java.net.preferIPv4Stack", "true");//TODO optional?

            server = new org.eclipse.jetty.server.Server();

            connector = new NetworkTrafficServerConnector(server);
            connector.setPort(httpPort.intValue());
            connector.setHost(host);
            server.addConnector(connector);

            ConfigurableResourceHandler resourceHandler = new ConfigurableResourceHandler(new FilesystemFileContentProvider(Paths.get("./src/main/resources/webapp")), () -> UUID.randomUUID().toString());


            GzipHandler gzipHandler = new GzipHandler();
            HashSet<String> mimeTypes = new HashSet<>();
            mimeTypes.add("text/html");
            mimeTypes.add("text/plain");
            mimeTypes.add("text/css");
            mimeTypes.add("application/x-javascript");
            mimeTypes.add("application/json");
            gzipHandler.setMinGzipSize(0);
//            gzipHandler.setMimeTypes(mimeTypes);

            HandlerCollection handlers = new HandlerCollection();
            handlers.setHandlers(new Handler[] { resourceHandler });
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



}
