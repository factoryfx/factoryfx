package io.github.factoryfx.docu.dependencyinjection;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final FactoryReferenceAttribute<RootFactory,Dependency,DependencyFactory> dependency =new FactoryReferenceAttribute<>();

    @Override
    public Root createImpl() {
        return new Root(dependency.instance());
    }
}
