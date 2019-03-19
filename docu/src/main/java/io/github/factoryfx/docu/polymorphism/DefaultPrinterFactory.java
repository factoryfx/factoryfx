package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.PolymorphicFactoryBase;

public class DefaultPrinterFactory extends PolymorphicFactoryBase<Printer,RootFactory> {
    @Override
    public Printer createImpl() {
        return new DefaultPrinter();
    }

}
