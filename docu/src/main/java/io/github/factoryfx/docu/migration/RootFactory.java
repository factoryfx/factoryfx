package io.github.factoryfx.docu.migration;

import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text");

    @Override
    public Root createImpl() {
        return new Root(text.get());
    }
}
