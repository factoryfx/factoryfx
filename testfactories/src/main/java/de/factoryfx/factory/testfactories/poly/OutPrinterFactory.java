package de.factoryfx.factory.testfactories.poly;

import de.factoryfx.factory.PolymorphicFactory;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class OutPrinterFactory extends PolymorphicFactoryBase<Printer,Void>  {
    @Override
    public Printer createImpl() {
        return new OutPrinter();
    }

}
