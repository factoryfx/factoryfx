package de.factoryfx.docu.restserver;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SimpleHttpServer extends SimpleFactoryBase<Server, Void, SimpleHttpServer> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<Void, SimpleHttpServer>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

    @Override
    public Server createImpl() {
        return server.instance();
    }
}