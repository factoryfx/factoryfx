package io.github.factoryfx.microservice.test.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

/** consumer service root: relays A's greeting through a typed remote reference */
public class BRoot extends SimpleFactoryBase<Server, BRoot> {
    public final FactoryAttribute<Server, JettyServerFactory<BRoot>> server = new FactoryAttribute<>();

    @Override
    protected Server createImpl() {
        return server.instance();
    }
}
