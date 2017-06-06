package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.SimpleFactoryBase;

public class ErrorPrinterFactory extends SimpleFactoryBase<Printer,Void> {

    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }
}
