package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.SimpleFactoryBase;

public class DefaultPrinterFactory extends SimpleFactoryBase<Printer,Void> {
    @Override
    public Printer createImpl() {
        return new DefaultPrinter();
    }
}
