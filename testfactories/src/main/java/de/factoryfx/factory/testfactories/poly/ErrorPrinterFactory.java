package de.factoryfx.factory.testfactories.poly;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;

public class ErrorPrinterFactory extends PolymorphicFactoryBase<Printer,ExampleFactoryA> {
    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }

}
