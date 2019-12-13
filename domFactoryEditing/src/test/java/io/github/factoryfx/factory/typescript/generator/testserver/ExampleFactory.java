package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.*;

public class ExampleFactory extends SimpleFactoryBase<Void, TestServerFactory> {
    public final StringAttribute stringAttribute=new StringAttribute().nullable();

    @Override
    protected Void createImpl() {
        return null;
    }
}