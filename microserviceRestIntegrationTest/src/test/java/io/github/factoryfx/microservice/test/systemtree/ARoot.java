package io.github.factoryfx.microservice.test.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

/** provider service root: serves TimeApi and its contract listing */
public class ARoot extends SimpleFactoryBase<Server, ARoot> {
    public final FactoryAttribute<Server, JettyServerFactory<ARoot>> server = new FactoryAttribute<>();

    @Override
    protected Server createImpl() {
        return server.instance();
    }
}
