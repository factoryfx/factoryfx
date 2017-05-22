package de.factoryfx.docu.dependencyinjection;

import de.factoryfx.factory.SimpleFactoryBase;

public class DependencyFactory extends SimpleFactoryBase<Dependency,Void> {
    @Override
    public Dependency createImpl() {
        return new Dependency();
    }
}
