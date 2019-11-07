package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    public final FactoryAttribute<Server, JettyServerFactory<ServerRootFactory>> httpServer = new FactoryAttribute<Server, JettyServerFactory<ServerRootFactory>>()
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    @Override
    protected Server createImpl() {
        return httpServer.instance();
    }

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
