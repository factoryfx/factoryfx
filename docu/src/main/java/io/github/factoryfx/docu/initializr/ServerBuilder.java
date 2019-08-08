package io.github.factoryfx.docu.initializr;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import java.lang.SuppressWarnings;
import org.eclipse.jetty.server.Server;

/**
 * Utility class to construct the factory tree */
public class ServerBuilder {
  private final FactoryTreeBuilder<Server, ServerRootFactory> builder;

  @SuppressWarnings("unchecked")
  public ServerBuilder() {
    this.builder= new FactoryTreeBuilder<>(ServerRootFactory.class);
    this.builder.addFactory(JettyServerFactory.class,Scope.SINGLETON,
            (ctx)-> new JettyServerBuilder<ServerRootFactory>()
                            .withHost("localhost").withPort(8080)
                            .build()
    );
    this.builder.addFactory(ExampleResourceFactory.class,Scope.SINGLETON);
    // register more factories here
  }

  public FactoryTreeBuilder<Server, ServerRootFactory> builder() {
    return this.builder;
  }
}
