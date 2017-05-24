package de.factoryfx.docu.migration;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root,Void>{
    public final StringAttribute text=new StringAttribute(new AttributeMetadata().labelText("Text"));

    @Override
    public Root createImpl() {
        return new Root(text.get());
    }
}
