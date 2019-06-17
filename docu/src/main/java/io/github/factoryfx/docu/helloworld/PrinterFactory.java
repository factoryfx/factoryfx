package io.github.factoryfx.docu.helloworld;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class PrinterFactory extends SimpleFactoryBase<Printer, PrinterFactory> {
    public final StringAttribute text=new StringAttribute();

    @Override
    protected Printer createImpl() {
        return new Printer(text.get());
    }
}
