package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object, PolymorphicFactoryExample> {
    public final FactoryPolymorphicAttribute<ExampleFactoryA,Printer> reference = new FactoryPolymorphicAttribute<ExampleFactoryA,Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    public final FactoryPolymorphicListAttribute<ExampleFactoryA,Printer> referenceList = new FactoryPolymorphicListAttribute<ExampleFactoryA,Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);

    @Override
    public Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
