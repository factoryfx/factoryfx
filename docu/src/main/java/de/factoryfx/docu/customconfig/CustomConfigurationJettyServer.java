package de.factoryfx.docu.customconfig;

import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import de.factoryfx.jetty.ServletBuilder;

public class CustomConfigurationJettyServer extends JettyServerFactory<Void, CustomConfigurationJettyServer> {
    public final FactoryReferenceAttribute<CustomConfigurationResource, CustomConfigurationResourceFactory> resource = new FactoryReferenceAttribute<>(CustomConfigurationResourceFactory.class);

    @Override
    protected void setupServlets(ServletBuilder servletBuilder) {
        defaultSetupServlets(servletBuilder,resource.instance());
    }
}
