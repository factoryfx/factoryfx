package io.github.factoryfx.docu.restserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SimpleHttpServer extends SimpleFactoryBase<Server, SimpleHttpServer> {
    public final FactoryReferenceAttribute<SimpleHttpServer,Server, JettyServerFactory<SimpleHttpServer>> server = new FactoryReferenceAttribute<>();

    @Override
    public Server createImpl() {
        return server.instance();
    }
}