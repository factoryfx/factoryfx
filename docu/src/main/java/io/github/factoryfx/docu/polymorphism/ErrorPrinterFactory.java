package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class ErrorPrinterFactory extends SimpleFactoryBase<Printer,RootFactory> {

    @Override
    protected Printer createImpl() {
        return new ErrorPrinter();
    }

}
