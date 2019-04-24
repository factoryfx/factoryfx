package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;

import java.util.List;

public interface AttributeCopy<V> {
    void internal_copyTo(AttributeCopy<V> copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,?> parent, FactoryBase<?,?> root);

    void internal_semanticCopyTo(AttributeCopy<V> copyAttribute);

    /**
     *
     * @param root factory tree root
     * @param parent data that contains the attribute
     */
    void internal_addBackReferences(FactoryBase<?,?> root, FactoryBase<?,?> parent);
    void set(V value);
}
