package de.factoryfx.jetty;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JettyServer {

    private final org.eclipse.jetty.server.Server server;
    private final Set<de.factoryfx.jetty.HttpServerConnectorCreator> currentConnectors = new HashSet<>();
    private final UpdateableServlet rootServlet;
    private boolean disposed = false;

    private static final Logger jerseyLogger1 = Logger.getLogger(org.glassfish.jersey.internal.inject.Providers.class.getName());
    private static final Logger jerseyLogger2 = Logger.getLogger(org.glassfish.jersey.internal.Errors.class.getName());
    static {
        jerseyLogger1.setLevel(Level.SEVERE); //another useless warning https://github.com/jersey/jersey/issues/3700
        jerseyLogger2.setLevel(Level.SEVERE);//warning about generic parameters, works fine and no fix available so the warnings are just useless
    }

    public JettyServer(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, ServletBuilder servletBuilder,
                       List<Handler> additionalHandlers) {


        this.server=new org.eclipse.jetty.server.Server();
        this.currentConnectors.addAll(connectors);
        for (de.factoryfx.jetty.HttpServerConnectorCreator creator : currentConnectors) {
            creator.addToServer(server);
        }

        this.rootServlet = new UpdateableServlet();
        updateWithServletBuilder(servletBuilder);
        ServletHolder holder = new ServletHolder(rootServlet);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( holder, "/*");
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setMinGzipSize(0);

        gzipHandler.setHandler(contextHandler);

        HandlerCollection handlers = new HandlerList();
        additionalHandlers.forEach(handlers::addHandler);
        handlers.addHandler(gzipHandler);
        server.setHandler(handlers);
    }

    private void updateWithServletBuilder(ServletBuilder servletBuilder) {
        HashMap<KeyedServlet,List<ServletPathSpec>> servlets = new HashMap<>();
        servletBuilder.forEachServletMapping((p,s)->{
            servlets.computeIfAbsent(new KeyedServlet(s), k->new ArrayList<>()).add(p);
        });
        this.rootServlet.update(servlets);
    }


    public JettyServer(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, ServletBuilder servletBuilder) {
        this(connectors,servletBuilder,new ArrayList<>());
    }


    private JettyServer(JettyServer priorServer) {
        this.rootServlet = priorServer.rootServlet;
        this.currentConnectors.addAll(priorServer.currentConnectors);
        this.server = priorServer.server;
        priorServer.disposed = true;
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
            if (Thread.interrupted())
                throw new RuntimeException("Interrupted");
            server.setStopTimeout(1L);
            server.stop();
            //because stop call can be inside this one of jetty's threads, we need to clear the interrupt
            //to let the rest of the factory updates run. They might be interrupt-sensitive
            Thread.interrupted();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isStopped() {
        return server.isStopped();
    }


    public JettyServer recreate(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, ServletBuilder servletBuilder) {
        JettyServer newServer = new JettyServer(this);
        newServer._recreate(connectors,servletBuilder);
        return newServer;
    }

    private void _recreate(List<de.factoryfx.jetty.HttpServerConnectorCreator> connectors, ServletBuilder servletBuilder) {
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
        updateWithServletBuilder(servletBuilder);
    }

}
