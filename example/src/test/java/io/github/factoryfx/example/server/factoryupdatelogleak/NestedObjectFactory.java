package io.github.factoryfx.example.server.factoryupdatelogleak;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class NestedObjectFactory extends SimpleFactoryBase<NestedObject, JettyServerRootFactory> {

    public final BooleanAttribute booleanAttribute = new BooleanAttribute();

    @Override
    protected NestedObject createImpl() {
        return new NestedObject(booleanAttribute.get());
    }
}
