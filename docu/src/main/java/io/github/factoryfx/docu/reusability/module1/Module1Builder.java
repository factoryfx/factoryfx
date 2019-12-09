package io.github.factoryfx.docu.reusability.module1;

import io.github.factoryfx.docu.reusability.base.DependencyFactory;
import io.github.factoryfx.docu.reusability.base.PrinterFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;

public class Module1Builder extends FactoryTreeBuilder<Void, RootFactory1> {
    protected Module1Builder() {
        super(RootFactory1.class, rootFactory1FactoryContext -> {
            RootFactory1 rootFactory1 = new RootFactory1();
            rootFactory1.printer.set(rootFactory1FactoryContext.getUnsafe(PrinterFactory.class));
            return rootFactory1;
        });
        addFactoryUnsafe(PrinterFactory.class, Scope.SINGLETON);
        addFactoryUnsafe(DependencyFactory.class, Scope.SINGLETON, rootFactory1FactoryContext -> {
            DependencyFactory<RootFactory1> dependencyFactory = new DependencyFactory<>();
            dependencyFactory.stringAttribute.set("mod1");
            return dependencyFactory;
        });
    }
}
