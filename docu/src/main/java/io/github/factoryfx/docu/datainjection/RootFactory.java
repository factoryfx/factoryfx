package io.github.factoryfx.docu.datainjection;

import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final StringAttribute text=new StringAttribute();
    public final FactoryAttribute<Printer,PrinterFactory> printer=new FactoryAttribute<>();

    @Override
    protected Root createImpl() {
        return new Root(text.get(),printer.instance());
    }
}
