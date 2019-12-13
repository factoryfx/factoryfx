package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

public class ServerBuilder {
    public FactoryTreeBuilder<Server, ServerRootFactory> builder(){
        FactoryTreeBuilder<Server, ServerRootFactory> factoryTreeBuilder = new FactoryTreeBuilder<>(ServerRootFactory.class);
        factoryTreeBuilder.addBuilder(ctx->
                new SimpleJettyServerBuilder<ServerRootFactory>()
                        .withHost("localhost").withPort(8089)
                        .withResource(ctx.get(GreetingsResourceFactory.class)));

        factoryTreeBuilder.addSingleton(GreetingsResourceFactory.class, context -> {
            GreetingsResourceFactory greetingsResourceFactory = new GreetingsResourceFactory();
            greetingsResourceFactory.backendClient.set(context.get(BackendClientFactory.class));
            return greetingsResourceFactory;
        });

        factoryTreeBuilder.addSingleton(BackendClientFactory.class, context -> {
            BackendClientFactory backendClientFactory = new BackendClientFactory();
            backendClientFactory.backendPort.set(18089);
            return backendClientFactory;
        });

        return factoryTreeBuilder;
    }
}
