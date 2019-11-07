package io.github.factoryfx.docu.rule.simulator;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SimulatorRootFactory extends SimulatorBaseFactory<Server> {

    public final FactoryAttribute<Server, JettyServerFactory<SimulatorRootFactory>> httpServer = new FactoryAttribute<Server, JettyServerFactory<SimulatorRootFactory>>()
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
