package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;

@FunctionalInterface
public interface NestedBuilder<L,R extends FactoryBase<L,R>>{
    void internal_build(FactoryTreeBuilder<L, R> builder);
}
