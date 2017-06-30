package de.factoryfx.docu.polymorphism;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class DefaultPrinterFactory extends PolymorphicFactoryBase<Printer,Void> {
    @Override
    public Printer createImpl() {
        return new DefaultPrinter();
    }

}
