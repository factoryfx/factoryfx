package io.github.factoryfx.example.server;


import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends JettyServerFactory<ServerRootFactory> {

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
