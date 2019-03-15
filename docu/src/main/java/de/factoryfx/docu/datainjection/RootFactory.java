package de.factoryfx.docu.datainjection;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text");

    @Override
    public Root createImpl() {
        return new Root(text.get());
    }
}
