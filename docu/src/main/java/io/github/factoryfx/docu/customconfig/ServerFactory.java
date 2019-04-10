package io.github.factoryfx.docu.customconfig;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerFactory extends SimpleFactoryBase<Server, ServerFactory> {
    public final FactoryAttribute<ServerFactory,Server, JettyServerFactory<ServerFactory>> server = new FactoryAttribute<>();

    @Override
    public Server createImpl() {
        return server.instance();
    }
}
