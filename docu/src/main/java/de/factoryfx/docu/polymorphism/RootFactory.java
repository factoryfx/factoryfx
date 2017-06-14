package de.factoryfx.docu.polymorphism;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,Void>{
    public final FactoryReferenceAttribute<Printer,SimpleFactoryBase<Printer,Void>> printer =new FactoryReferenceAttribute<Printer,SimpleFactoryBase<Printer,Void>>().setupUnsafe(SimpleFactoryBase.class).labelText("dependency");

    @Override
    public Root createImpl() {
        return new Root(printer.instance());
    }
}
