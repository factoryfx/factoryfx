package de.factoryfx.jetty;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.function.Consumer;

public class DefaultResourceConfigSetup implements Consumer<ResourceConfig> {

    @Override
    public void accept(ResourceConfig resourceConfig) {
        resourceConfig.register(new AllExceptionMapper());
    }

}
