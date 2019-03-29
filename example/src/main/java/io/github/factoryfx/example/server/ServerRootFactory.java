package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.ShopJettyServerFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    public final FactoryReferenceAttribute<ServerRootFactory, Server, ShopJettyServerFactory> httpServer = new FactoryReferenceAttribute<ServerRootFactory, Server, ShopJettyServerFactory>()
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
