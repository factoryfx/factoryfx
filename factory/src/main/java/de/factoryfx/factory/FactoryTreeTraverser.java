package de.factoryfx.factory;

import com.google.common.collect.TreeTraverser;

class FactoryTreeTraverser<V> extends TreeTraverser<FactoryBase<?,V>> {
    @Override
    public Iterable<FactoryBase<?,V>> children(FactoryBase<?,V> factory) {
        return factory;
    }
}
