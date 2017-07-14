package de.factoryfx.factory.testfactories.poly;

import de.factoryfx.factory.PolymorphicFactoryBase;

public class OutPrinterFactory extends PolymorphicFactoryBase<Printer,Void>  {
    @Override
    public Printer createImpl() {
        return new OutPrinter();
    }

}
