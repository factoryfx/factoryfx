package io.github.factoryfx.docu.configurationwebapp;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SimpleHttpServer extends SimpleFactoryBase<Server, SimpleHttpServer> {
    public final FactoryAttribute<Server, JettyServerFactory<SimpleHttpServer>> server = new FactoryAttribute<>();

    @Override
    protected Server createImpl() {
        return server.instance();
    }
}