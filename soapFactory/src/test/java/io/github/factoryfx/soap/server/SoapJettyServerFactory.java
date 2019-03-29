package io.github.factoryfx.soap.server;


import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SoapJettyServerFactory extends SimpleFactoryBase<Server, SoapJettyServerFactory> {
    public final FactoryReferenceAttribute<SoapJettyServerFactory, Server, JettyServerFactory<SoapJettyServerFactory>> server = new FactoryReferenceAttribute<>();

    @Override
    public Server createImpl() {
        return server.instance();
    }
}
