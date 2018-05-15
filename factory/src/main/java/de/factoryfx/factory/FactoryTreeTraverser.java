package de.factoryfx.factory;

import com.google.common.collect.TreeTraverser;

class FactoryTreeTraverser<V,R extends FactoryBase<?,V,R>> extends TreeTraverser<FactoryBase<?,V,R>> {
    @Override
    public Iterable<FactoryBase<?,V,R>> children(FactoryBase<?,V,R> factory) {
        return factory;
    }
}
