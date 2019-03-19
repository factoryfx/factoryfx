package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.ShopJettyServerFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, ShopJettyServerFactory> httpServer = new FactoryReferenceAttribute<>(ShopJettyServerFactory.class)
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
