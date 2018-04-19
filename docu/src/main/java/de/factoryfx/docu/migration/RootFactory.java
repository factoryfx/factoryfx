package de.factoryfx.docu.migration;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root,Void, RootFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text");

    @Override
    public Root createImpl() {
        return new Root(text.get());
    }
}
