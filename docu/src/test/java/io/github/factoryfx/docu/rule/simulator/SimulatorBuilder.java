package io.github.factoryfx.docu.rule.simulator;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Server;

public class SimulatorBuilder {
    public FactoryTreeBuilder<Server, SimulatorRootFactory, Void> builder(){
        FactoryTreeBuilder<Server, SimulatorRootFactory, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(SimulatorRootFactory.class);

        factoryTreeBuilder.addFactory(HelloJettyServerFactory.class, Scope.SINGLETON, context -> {
            return new JettyServerBuilder<>(new HelloJettyServerFactory())
                    .withHost("localhost").withPort(18089)
                    .withResource(context.get(HelloResourceFactory.class)).build();

        });

        factoryTreeBuilder.addFactory(HelloResourceFactory.class, Scope.SINGLETON);

        return factoryTreeBuilder;
    }
}
