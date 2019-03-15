package de.factoryfx.factory.testfactories.poly;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.testfactories.ExampleFactoryA;

public class OutPrinterFactory extends PolymorphicFactoryBase<Printer,ExampleFactoryA>  {
    @Override
    public Printer createImpl() {
        return new OutPrinter();
    }

}
