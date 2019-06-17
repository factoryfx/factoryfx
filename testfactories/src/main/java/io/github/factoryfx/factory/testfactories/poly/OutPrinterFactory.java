package io.github.factoryfx.factory.testfactories.poly;

import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;

public class OutPrinterFactory extends PolymorphicFactoryBase<Printer,ExampleFactoryA> {
    @Override
    protected Printer createImpl() {
        return new OutPrinter();
    }

}
