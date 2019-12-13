package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.poly.Printer;

public class PolymorphicFactoryExample extends SimpleFactoryBase<Object, PolymorphicFactoryExample> {
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<>();
    public final FactoryPolymorphicListAttribute<Printer> referenceList = new FactoryPolymorphicListAttribute<>();

    @Override
    protected Object createImpl() {
        reference.instance().print();
        referenceList.instances().forEach(Printer::print);
        return new Object();
    }
}
