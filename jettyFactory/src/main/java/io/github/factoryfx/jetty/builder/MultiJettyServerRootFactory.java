package io.github.factoryfx.jetty.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;

import java.util.List;

public class MultiJettyServerRootFactory extends SimpleFactoryBase<List<Server>,MultiJettyServerRootFactory> {

    public final FactoryListAttribute<Server, JettyServerFactory<MultiJettyServerRootFactory>> servers = new FactoryListAttribute<>();

    @Override
    protected List<Server> createImpl() {
        return servers.instances();
    }


}
