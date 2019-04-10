package io.github.factoryfx.docu.polymorphism;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryPolymorphicAttribute<RootFactory,Printer> printer =new FactoryPolymorphicAttribute<RootFactory,Printer>().setup(Printer.class,ErrorPrinterFactory.class,DefaultPrinterFactory.class);

    @Override
    public Root createImpl() {
        return new Root(printer.instance());
    }
}
