package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    public final FactoryAttribute<ServerRootFactory, Server, GreetingsJettyServerFactory> httpServer = new FactoryAttribute<ServerRootFactory, Server, GreetingsJettyServerFactory>()
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    public Server createImpl() {
        return httpServer.instance();
    }

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
