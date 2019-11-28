package io.github.factoryfx.docu.reusability.base;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;

public class DependencyFactory<R extends FactoryBase<?, R>> extends FactoryBase<Dependency, R> {
    public  final StringAttribute stringAttribute = new StringAttribute();
    public DependencyFactory() {
        configLifeCycle().setCreator(()->new Dependency(stringAttribute.get()));
    }
}
