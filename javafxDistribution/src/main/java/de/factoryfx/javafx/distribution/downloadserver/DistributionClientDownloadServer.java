package de.factoryfx.javafx.distribution.downloadserver;

import java.io.File;

import de.factoryfx.factory.LiveObject;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class DistributionClientDownloadServer<T> implements LiveObject<T> {

    private final org.eclipse.jetty.server.Server server;
    private final NetworkTrafficServerConnector connector;

    public DistributionClientDownloadServer(String host, int port, String distributionClientBasePath) {
        server=new org.eclipse.jetty.server.Server();

        connector = new NetworkTrafficServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);

        server.setConnectors(new Connector[]{connector});


        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newResource(new File(distributionClientBasePath)));


        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
    }

    @Override
    public void start(){
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop(){
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
