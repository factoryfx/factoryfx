package io.github.factoryfx.docu.initializr;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;

import java.lang.SuppressWarnings;

import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

/**
 * Utility class to construct the factory tree */
public class ServerBuilder {
  private final FactoryTreeBuilder<Server, ServerRootFactory> builder;

  public ServerBuilder() {
    this.builder= new FactoryTreeBuilder<>(ServerRootFactory.class);

    new SimpleJettyServerBuilder<ServerRootFactory>()
            .withHost("localhost").withPort(8080)
            .internal_build(builder);

    this.builder.addFactory(ExampleResourceFactory.class,Scope.SINGLETON);
    // register more factories here
  }

  public FactoryTreeBuilder<Server, ServerRootFactory> builder() {
    return this.builder;
  }
}
