package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.PolymorphicFactoryBase;

public class DefaultPrinterFactory extends PolymorphicFactoryBase<Printer,Void,RootFactory> {
    @Override
    public Printer createImpl() {
        return new DefaultPrinter();
    }

}
