package de.factoryfx.soap.server;


import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.*;
import org.eclipse.jetty.server.Server;

public class SoapJettyServerFactory extends SimpleFactoryBase<Server, Void, SoapJettyServerFactory> {
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<Server, JettyServerFactory<Void, SoapJettyServerFactory>> server = FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(JettyServerFactory.class));

    @Override
    public Server createImpl() {
        return server.instance();
    }
}
