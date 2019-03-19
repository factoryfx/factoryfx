package io.github.factoryfx.docu.dependencyinjection;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root, RootFactory> {
    public final FactoryReferenceAttribute<Dependency,DependencyFactory> dependency =new FactoryReferenceAttribute<>(DependencyFactory.class).labelText("dependency");

    @Override
    public Root createImpl() {
        return new Root(dependency.instance());
    }
}
