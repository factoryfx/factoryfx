package de.factoryfx.factory.testfactories.poly;

import de.factoryfx.factory.PolymorphicFactoryBase;

public class ErrorPrinterFactory extends PolymorphicFactoryBase<Printer,Void> {
    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }

}
