package io.github.factoryfx.docu.initializr;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import org.eclipse.jetty.server.Server;

/**
 * Utility class to construct the factory tree */
public class ServerBuilder {
  private final FactoryTreeBuilder<Server, ServerRootFactory> builder;

  public ServerBuilder() {
    this.builder= new FactoryTreeBuilder<>(ServerRootFactory.class);
    this.builder.addBuilder((ctx)->
                    new SimpleJettyServerBuilder<ServerRootFactory>()
                            .withHost("localhost").withPort(8080)
                            .withResource(new FactoryTemplateId<>(ExampleResourceFactory.class)));
    this.builder.addSingleton(ExampleResourceFactory.class);
    // register more factories here
  }

  public FactoryTreeBuilder<Server, ServerRootFactory> builder() {
    return this.builder;
  }
}
