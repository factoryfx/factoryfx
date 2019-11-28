package io.github.factoryfx.docu.reusability.module1;

import io.github.factoryfx.docu.reusability.base.DependencyFactory;
import io.github.factoryfx.docu.reusability.base.PrinterFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;

public class Module1Builder extends FactoryTreeBuilder<Void, RootFactory1> {
    protected Module1Builder() {
        super(RootFactory1.class, rootFactory1FactoryContext -> {
            RootFactory1 rootFactory1 = new RootFactory1();
            rootFactory1.printer.set(rootFactory1FactoryContext.get(PrinterFactory.class));
            return rootFactory1;
        });
        addSingleton(PrinterFactory.class);
        addSingleton(DependencyFactory.class, rootFactory1FactoryContext -> {
            DependencyFactory dependencyFactory = new DependencyFactory();
            dependencyFactory.stringAttribute.set("mod1");
            return dependencyFactory;
        });
    }
}
