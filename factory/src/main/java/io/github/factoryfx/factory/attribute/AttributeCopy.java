package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;

import java.util.List;
import java.util.function.Function;

public interface AttributeCopy<V> {
    void internal_copyTo(AttributeCopy<V> copyAttribute, Function<FactoryBase<?,?>,FactoryBase<?,?>> newCopyInstanceProvider,  final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,?> parent, FactoryBase<?,?> root);

    /**
     *
     * @param root factory tree root
     * @param parent data that contains the attribute
     */
    void internal_addBackReferences(FactoryBase<?,?> root, FactoryBase<?,?> parent);
    void set(V value);
}
