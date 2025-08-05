package io.github.factoryfx.example.server.factoryupdatelogleak;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class SimplifiedMicroserviceResourceFactory extends SimpleFactoryBase<SimplifiedMicroserviceResource, JettyServerRootFactory> {
    @Override
    protected SimplifiedMicroserviceResource createImpl() {
        return new SimplifiedMicroserviceResource(utility().getMicroservice());
    }
}
