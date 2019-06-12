package io.github.factoryfx.docu.rule.simulator;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import org.eclipse.jetty.server.Server;

public class SimulatorRootFactory extends SimulatorBaseFactory<Server> {

    public final FactoryAttribute<SimulatorRootFactory, Server, HelloJettyServerFactory> httpServer = new FactoryAttribute<SimulatorRootFactory, Server, HelloJettyServerFactory>()
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    @Override
    protected Server createImpl() {
        return httpServer.instance();
    }

    public SimulatorRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
