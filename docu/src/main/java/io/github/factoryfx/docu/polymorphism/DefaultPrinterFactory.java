package io.github.factoryfx.docu.polymorphism;


import io.github.factoryfx.factory.SimpleFactoryBase;

public class DefaultPrinterFactory extends SimpleFactoryBase<Printer,RootFactory> {
    @Override
    protected Printer createImpl() {
        return new DefaultPrinter();
    }

}
