package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Function;

public class DataMerger<R extends FactoryBase<?,R>> {

    private final R currentData;
    private final R commonData;
    private final R newData;

    private final Map<UUID, FactoryBase<?,R>> currentMap;
    private final Map<UUID, FactoryBase<?,R>> originalMap;
    private final Map<UUID, FactoryBase<?,R>> newMap;

    public DataMerger(R currentData, R commonData, R newData) {
        if (currentData==commonData){
            throw new IllegalArgumentException("Arguments: currentData and commonData can't be the same, use .utility().copy() to create a copy");
        }

        currentData.internal().addBackReferences();
        commonData.internal().addBackReferences();
        newData.internal().addBackReferences();

        this.currentData = currentData;
        this.commonData = commonData;
        this.newData = newData;

        this.currentMap = currentData.internal().collectChildFactoryMap();
        this.originalMap = commonData.internal().collectChildFactoryMap();
        this.newMap = newData.internal().collectChildFactoryMap();

//        List<FactoryBase<?,R>> currentList = currentData.internal().collectChildrenDeep();
//        List<FactoryBase<?,R>> originalList = commonData.internal().collectChildrenDeep();
//        List<FactoryBase<?,R>> newMapList = commonData.internal().collectChildrenDeep();
//        Collections.sort(currentList, Comparator.comparing(FactoryBase::getId));
//        Collections.sort(originalList, Comparator.comparing(FactoryBase::getId));
//        Collections.sort(newMapList, Comparator.comparing(FactoryBase::getId));


        for (FactoryBase<?,R> newDataChild : newMap.values()) {//avoid mix up with iteration counters
            newDataChild.internal().resetIterationCounterFlat();
        }
    }

    @SuppressWarnings("unchecked")
    public MergeResult<R> createMergeResult(Function<String,Boolean> permissionChecker) {
        MergeResult<R> mergeResult = new MergeResult<>(currentData);

        for (Map.Entry<UUID, FactoryBase<?,R>> entry : currentMap.entrySet()) {
            FactoryBase originalValue = getOriginalValue(entry);
            FactoryBase newValue = getNewValue(entry);

            if (newValue==null && originalValue!=null){
                //check for conflict for removed object
                entry.getValue().internal().visitAttributesForMatch(originalValue, (name,currentAttribute, originalAttribute) -> {
                    if (!currentAttribute.internal_mergeMatch(originalAttribute)){
                        mergeResult.addConflictInfo(new AttributeDiffInfo(name,entry.getValue().getId()));
                    }
                    return true;
                });
            }

            if (originalValue!=null && newValue!=null){
                entry.getValue().internal().merge(originalValue, newValue, mergeResult, permissionChecker);
            }
        }
        return mergeResult;
    }

    private FactoryBase<?,R> getNewValue(Map.Entry<UUID, FactoryBase<?,R>> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return newData;
        }
        return newMap.get(currentEntry.getKey());
    }

    private FactoryBase<?,R> getOriginalValue(Map.Entry<UUID, FactoryBase<?,R>> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return commonData;
        }
        return originalMap.get(currentEntry.getKey());
    }

    public MergeDiffInfo<R> mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        return createMergeResult(permissionChecker).executeMerge();
    }

}
