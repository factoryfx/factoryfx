package io.github.factoryfx.docu.rule.simulator;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

public class SimulatorBuilder {
    public FactoryTreeBuilder<Server, SimulatorRootFactory> builder(){
        FactoryTreeBuilder<Server, SimulatorRootFactory> builder = new FactoryTreeBuilder<>(SimulatorRootFactory.class);
        builder.addBuilder(ctx->
            new SimpleJettyServerBuilder<SimulatorRootFactory>()
                    .withHost("localhost").withPort(18089)
                    .withResource(ctx.get(HelloResourceFactory.class))
        );
        builder.addFactory(HelloResourceFactory.class, Scope.SINGLETON);
        return builder;
    }
}
