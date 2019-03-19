package io.github.factoryfx.factory.atrribute;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object,PolymorphicFactoryExample> {
    public final FactoryPolymorphicReferenceAttribute<Printer> reference = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    public final FactoryPolymorphicReferenceListAttribute<Printer> referenceList = new FactoryPolymorphicReferenceListAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

    @Override
    public Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
