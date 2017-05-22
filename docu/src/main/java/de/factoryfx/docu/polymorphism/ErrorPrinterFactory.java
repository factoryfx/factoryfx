package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import org.glassfish.hk2.api.Factory;

public class ErrorPrinterFactory extends SimpleFactoryBase<Printer,Void> {

    @Override
    public Printer createImpl() {
        return new ErrorPrinter();
    }
}
