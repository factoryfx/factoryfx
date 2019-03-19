package io.github.factoryfx.docu.monitoring;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class SimpleResourceFactory extends SimpleFactoryBase<SimpleResource,RootFactory> {
    @Override
    public SimpleResource createImpl() {
        return new SimpleResource();
    }
}
