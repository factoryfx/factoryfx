package io.github.factoryfx.factory.attribute;

import io.github.factoryfx.factory.FactoryBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface AttributeMerger<V> extends AttributeMatch<V> {

    void internal_merge(V newValue);

    boolean internal_hasWritePermission(Function<String,Boolean> permissionChecker);

    default <F extends FactoryBase<?,?>>  void internal_mergeFactoryList(List<F> oldList, List<F> newList) {
        //keep old factories for the state
        Map<UUID, F> previousMap=new HashMap<>();
        for (F item : oldList) {
            previousMap.put(item.getId(),item);
        }
        oldList.clear();

        for (F newItem : newList) {
            F oldItem = previousMap.get(newItem.getId());
            if (oldItem!=null){
                oldList.add(oldItem);
            } else {
                oldList.add(newItem);
            }
        }
    }
}
