package io.github.factoryfx.docu.datainjection;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class PrinterFactory extends SimpleFactoryBase<Printer, RootFactory> {

    @Override
    protected Printer createImpl() {
        return new Printer();
    }
}
