package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryPolymorphicReferenceAttribute<Printer> printer =new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,DefaultPrinterFactory.class).labelText("dependency");

    @Override
    public Root createImpl() {
        return new Root(printer.instance());
    }
}
