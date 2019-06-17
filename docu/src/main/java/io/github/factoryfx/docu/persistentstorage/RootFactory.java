package io.github.factoryfx.docu.persistentstorage;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    @Override
    protected Root createImpl() {
        return new Root(stringAttribute.get());
    }
}
