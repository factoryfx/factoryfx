package io.github.factoryfx.docu.reusability.module2;

import io.github.factoryfx.docu.reusability.base.DependencyFactory;
import io.github.factoryfx.docu.reusability.base.PrinterFactory;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;

public class Module2Builder extends FactoryTreeBuilder<Void, RootFactory2> {
    protected Module2Builder() {
        super(RootFactory2.class, rootFactory1FactoryContext -> {
            RootFactory2 rootFactory2 = new RootFactory2();
            rootFactory2.printer.set(rootFactory1FactoryContext.getUnsafe(PrinterFactory.class));
            return rootFactory2;
        });
        addFactoryUnsafe(PrinterFactory.class, Scope.SINGLETON);
        addFactoryUnsafe(DependencyFactory.class, Scope.SINGLETON, rootFactory1FactoryContext -> {
            DependencyFactory<RootFactory2> dependencyFactory = new DependencyFactory<>();
            dependencyFactory.stringAttribute.set("mod2");
            return dependencyFactory;
        });
    }
}
