package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object, ExampleFactoryA> {
    public final FactoryPolymorphicReferenceAttribute<ExampleFactoryA,Printer> reference = new FactoryPolymorphicReferenceAttribute<ExampleFactoryA,Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    public final FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer> referenceList = new FactoryPolymorphicReferenceListAttribute<ExampleFactoryA,Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

    @Override
    public Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
