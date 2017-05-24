package de.factoryfx.docu.persistentstorage;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root,Void> {
    public final StringAttribute stringAttribute = new StringAttribute(new AttributeMetadata());

    @Override
    public Root createImpl() {
        return new Root(stringAttribute.get());
    }
}
