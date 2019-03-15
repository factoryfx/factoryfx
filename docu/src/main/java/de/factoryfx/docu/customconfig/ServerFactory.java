package de.factoryfx.docu.customconfig;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class ServerFactory extends SimpleFactoryBase<Server, ServerFactory> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<ServerFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

    @Override
    public Server createImpl() {
        return server.instance();
    }
}
