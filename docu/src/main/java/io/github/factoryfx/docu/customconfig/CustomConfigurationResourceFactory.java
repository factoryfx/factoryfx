package io.github.factoryfx.docu.customconfig;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class CustomConfigurationResourceFactory extends SimpleFactoryBase<CustomConfigurationResource, JettyServerRootFactory> {

    @Override
    protected CustomConfigurationResource createImpl() {
        return new CustomConfigurationResource(this.utility().getMicroservice());
    }

}
