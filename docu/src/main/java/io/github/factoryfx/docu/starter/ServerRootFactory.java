package io.github.factoryfx.docu.starter;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.JettyServerFactory;
import java.lang.Override;
import org.eclipse.jetty.server.Server;

/**
 * Root factory of the project */
public class ServerRootFactory extends SimpleFactoryBase<Server, ServerRootFactory> {
  public final FactoryAttribute<Server, JettyServerFactory<ServerRootFactory>> jettyServer = new FactoryAttribute<>();

  @Override
  public Server createImpl() {
    return jettyServer.instance();
  }
}
