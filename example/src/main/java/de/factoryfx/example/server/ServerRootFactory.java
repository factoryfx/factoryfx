package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.*;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

import de.factoryfx.jetty.JettyServer;
import de.factoryfx.jetty.JettyServerFactory;

public class ServerRootFactory extends ServerBaseFactory<JettyServer> {

    public final FactoryReferenceAttribute<JettyServer, ShopJettyServerFactory> httpServer =
            new FactoryReferenceAttribute<JettyServer, ShopJettyServerFactory>().setupUnsafe(JettyServerFactory.class)
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    public JettyServer createImpl() {
        return httpServer.instance();
    }
}
