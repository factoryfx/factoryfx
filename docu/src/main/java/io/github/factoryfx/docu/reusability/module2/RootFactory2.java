package io.github.factoryfx.docu.reusability.module2;

import io.github.factoryfx.docu.reusability.base.Printer;
import io.github.factoryfx.docu.reusability.base.PrinterFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class RootFactory2 extends FactoryBase<Void, RootFactory2> {
    FactoryAttribute<Printer, PrinterFactory<RootFactory2>> printer = new FactoryAttribute<>();
    // other dependencies for the rootfactory of module 2
}
