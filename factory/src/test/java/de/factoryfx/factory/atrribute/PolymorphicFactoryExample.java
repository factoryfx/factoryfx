package de.factoryfx.factory.atrribute;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import de.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import de.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object,Void> {
    public final FactoryPolymorphicReferenceAttribute<Printer> reference = new FactoryPolymorphicReferenceAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    public final FactoryPolymorphicReferenceListAttribute<Printer> referenceList = new FactoryPolymorphicReferenceListAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

    @Override
    public Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
