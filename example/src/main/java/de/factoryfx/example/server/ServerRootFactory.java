package de.factoryfx.example.server;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<ServerRootFactory>> httpServer =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class)
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly());

    public Server createImpl() {
        return httpServer.instance();
    }

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
