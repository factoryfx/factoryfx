package io.github.factoryfx.docu.dependencyinjection;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final FactoryAttribute<RootFactory,Dependency,DependencyFactory> dependency =new FactoryAttribute<>();

    @Override
    public Root createImpl() {
        return new Root(dependency.instance());
    }
}
