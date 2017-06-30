package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class DefaultPrinterFactory extends PolymorphicFactoryBase<Printer,Void> {
    @Override
    public Printer createImpl() {
        return new DefaultPrinter();
    }

    @Override
    public Class<Printer> getLiveObjectClass() {
        return Printer.class;
    }
}
