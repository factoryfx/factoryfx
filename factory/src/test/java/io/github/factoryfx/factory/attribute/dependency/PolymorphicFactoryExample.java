package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object, PolymorphicFactoryExample> {
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    public final FactoryPolymorphicListAttribute<Printer> referenceList = new FactoryPolymorphicListAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

    @Override
    protected Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
