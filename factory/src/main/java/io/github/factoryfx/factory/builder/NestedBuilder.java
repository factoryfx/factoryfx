package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;

@FunctionalInterface
public interface NestedBuilder<R extends FactoryBase<?,R>>{
    void internal_build(FactoryTreeBuilder<?, R> builder);
}
