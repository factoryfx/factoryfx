package io.github.factoryfx.docu.customconfig;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerFactory extends SimpleFactoryBase<Server, ServerFactory> {
    public final FactoryReferenceAttribute<ServerFactory,Server, JettyServerFactory<ServerFactory>> server = new FactoryReferenceAttribute<>();

    @Override
    public Server createImpl() {
        return server.instance();
    }
}
