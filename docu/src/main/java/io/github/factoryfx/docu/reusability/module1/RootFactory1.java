package io.github.factoryfx.docu.reusability.module1;

import io.github.factoryfx.docu.reusability.base.Printer;
import io.github.factoryfx.docu.reusability.base.PrinterFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class RootFactory1 extends FactoryBase<Void, RootFactory1> {
    public final FactoryAttribute<Printer, PrinterFactory<RootFactory1>> printer = new FactoryAttribute<>();
    // other dependencies for the rootfactory of module 1
}
