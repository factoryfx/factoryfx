package de.factoryfx.docu.persistentstorage;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    @Override
    public Root createImpl() {
        return new Root(stringAttribute.get());
    }
}
