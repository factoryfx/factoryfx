package de.factoryfx.docu.helloworld;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class PrinterFactory extends FactoryBase<Printer,Void, PrinterFactory> {
    public final StringAttribute text=new StringAttribute().labelText("Text");

    public PrinterFactory(){
        configLiveCycle().setCreator(() -> new Printer(text.get()));
        configLiveCycle().setStarter(Printer::print);
    }

}
