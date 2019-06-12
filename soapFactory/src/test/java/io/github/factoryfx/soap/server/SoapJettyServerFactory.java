package io.github.factoryfx.soap.server;


import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

public class SoapJettyServerFactory extends SimpleFactoryBase<Server, SoapJettyServerFactory> {
    public final FactoryAttribute<SoapJettyServerFactory, Server, JettyServerFactory<SoapJettyServerFactory>> server = new FactoryAttribute<>();

    @Override
    protected Server createImpl() {
        return server.instance();
    }
}
