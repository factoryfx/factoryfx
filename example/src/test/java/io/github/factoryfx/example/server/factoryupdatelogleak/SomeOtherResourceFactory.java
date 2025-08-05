package io.github.factoryfx.example.server.factoryupdatelogleak;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class SomeOtherResourceFactory extends SimpleFactoryBase<SomeOtherResource, JettyServerRootFactory> {
    public final FactoryAttribute<NestedObject, NestedObjectFactory> nested = new FactoryAttribute<>();

    @Override
    protected SomeOtherResource createImpl() {
        return new SomeOtherResource(nested.instance());
    }
}
