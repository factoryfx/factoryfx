package io.github.factoryfx.factory;

import io.github.factoryfx.factory.merge.MergeDiffInfo;

import java.util.Map;
import java.util.UUID;

@FunctionalInterface
public interface FactoryUpdateMerge<R extends FactoryBase<?,R>> {
    MergeDiffInfo<R> update(R root, Map<UUID,FactoryBase<?,R>> idToFactory);
}
