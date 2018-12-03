package de.factoryfx.docu.customconfig;

import de.factoryfx.factory.SimpleFactoryBase;

public class CustomConfigurationResourceFactory extends SimpleFactoryBase<CustomConfigurationResource,Void, CustomConfigurationJettyServer> {

    @Override
    public CustomConfigurationResource createImpl() {
        return new CustomConfigurationResource(this.utilityFactory().getMicroservice());
    }

}
