package io.github.factoryfx.docu.customconfig;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class CustomConfigurationResourceFactory extends SimpleFactoryBase<CustomConfigurationResource, ServerFactory> {

    @Override
    protected CustomConfigurationResource createImpl() {
        return new CustomConfigurationResource(this.utility().getMicroservice());
    }

}
