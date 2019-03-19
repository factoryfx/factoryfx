package io.github.factoryfx.factory.testfactories.poly;

import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;

public class ErrorPrinterFactory extends PolymorphicFactoryBase<Printer,ExampleFactoryA> {
    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }

}
