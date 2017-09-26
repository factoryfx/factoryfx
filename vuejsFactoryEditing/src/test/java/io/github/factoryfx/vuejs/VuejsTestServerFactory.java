package io.github.factoryfx.vuejs;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;

public class VuejsTestServerFactory extends SimpleFactoryBase<VuejsTestServer, Void> {

    public final FactoryReferenceAttribute<JettyServer, JettyServerFactory<Void>> jettyServer = new FactoryReferenceAttribute<JettyServer, JettyServerFactory<Void>>().setupUnsafe(JettyServerFactory.class);

    @Override
    public VuejsTestServer createImpl() {
        return new VuejsTestServer(jettyServer.instance());
    }
}