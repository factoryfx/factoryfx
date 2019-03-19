package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.PolymorphicFactoryBase;

public class ErrorPrinterFactory extends PolymorphicFactoryBase<Printer,RootFactory> {

    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }

}
