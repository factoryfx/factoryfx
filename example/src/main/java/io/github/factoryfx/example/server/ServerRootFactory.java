package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.ShopJettyServerFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    public final FactoryAttribute<ServerRootFactory, Server, ShopJettyServerFactory> httpServer = new FactoryAttribute<ServerRootFactory, Server, ShopJettyServerFactory>()
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
