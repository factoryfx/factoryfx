package io.github.factoryfx.docu.reusability.base;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class PrinterFactory<R extends FactoryBase<?, R>> extends FactoryBase<Printer, R> {
    public final FactoryAttribute<Dependency, DependencyFactory<R>> dependencyFactory = new FactoryAttribute<Dependency, DependencyFactory<R>>();

    public PrinterFactory() {
        configLifeCycle().setCreator(() -> new Printer(dependencyFactory.instance()));
    }
}
