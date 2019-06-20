package io.github.factoryfx.docu.rule.server;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Server;

public class ServerBuilder {
    public FactoryTreeBuilder<Server, ServerRootFactory, Void> builder(){
        FactoryTreeBuilder<Server, ServerRootFactory, Void> factoryTreeBuilder = new FactoryTreeBuilder<>(ServerRootFactory.class);

        factoryTreeBuilder.addFactory(GreetingsJettyServerFactory.class, Scope.SINGLETON, context ->
                new JettyServerBuilder<ServerRootFactory>()
                        .withHost("localhost").withPort(8089)
                        .withResource(context.get(GreetingsResourceFactory.class))
                        .buildTo(new GreetingsJettyServerFactory()));

        factoryTreeBuilder.addFactory(GreetingsResourceFactory.class, Scope.SINGLETON, context -> {
            GreetingsResourceFactory greetingsResourceFactory = new GreetingsResourceFactory();
            greetingsResourceFactory.backendClient.set(context.get(BackendClientFactory.class));
            return greetingsResourceFactory;
        });

        factoryTreeBuilder.addFactory(BackendClientFactory.class, Scope.SINGLETON, context -> {
            BackendClientFactory backendClientFactory = new BackendClientFactory();
            backendClientFactory.backendPort.set(18089);
            return backendClientFactory;
        });

        return factoryTreeBuilder;
    }
}
