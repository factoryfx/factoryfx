package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryPolymorphicReferenceAttribute<RootFactory,Printer> printer =new FactoryPolymorphicReferenceAttribute<RootFactory,Printer>().setup(Printer.class,ErrorPrinterFactory.class,DefaultPrinterFactory.class);

    @Override
    public Root createImpl() {
        return new Root(printer.instance());
    }
}
