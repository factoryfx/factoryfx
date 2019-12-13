package io.github.factoryfx.docu.mock;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class PrinterFactory extends FactoryBase<Printer, PrinterFactory> {
    public final StringAttribute text=new StringAttribute();

    public PrinterFactory(){
        configLifeCycle().setCreator(() -> new Printer(text.get()));
        configLifeCycle().setStarter(Printer::print);
    }

}
