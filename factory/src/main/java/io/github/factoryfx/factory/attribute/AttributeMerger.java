package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;

import java.util.List;
import java.util.function.Function;

public interface AttributeMerger<V> extends AttributeMatch<V> {

    void internal_merge(V newValue);
    boolean internal_hasWritePermission(Function<String,Boolean> permissionChecker);

    default <F extends FactoryBase<?,?>>  void internal_mergeFactoryList(List<F> oldList, List<F> newList) {
        oldList.clear();
        oldList.addAll(newList);
    }
}
