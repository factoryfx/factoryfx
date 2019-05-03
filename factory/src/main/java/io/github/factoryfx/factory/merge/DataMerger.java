package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Function;

public class DataMerger<R extends FactoryBase<?,R>> {

    private final R currentData;
    private final R commonData;
    private final R newData;

    private Map<UUID, FactoryBase<?,R>> currentMap;
    private Map<UUID, FactoryBase<?,R>> commonMap;
    private Map<UUID, FactoryBase<?,R>> newMap;

    public DataMerger(R currentData, R commonData, R newData) {
        if (currentData==commonData){
            throw new IllegalArgumentException("Arguments: currentData and commonData can't be the same, use .utility().copy() to create a copy");
        }

        this.currentData = currentData;
        this.commonData = commonData;
        this.newData = newData;


        Thread currentThread = new Thread(() -> {
            currentData.internal().addBackReferences();
            currentMap = currentData.internal().collectChildFactoryMap();
        });
        currentThread.start();
        Thread commonThread = new Thread(() -> {
            commonData.internal().addBackReferences();
            commonMap = commonData.internal().collectChildFactoryMap();
        });
        commonThread.start();
        Thread newThread = new Thread(() -> {
            newData.internal().addBackReferences();
            newMap = newData.internal().collectChildFactoryMap();
        });
        newThread.start();
        try {
            currentThread.join();
            commonThread.join();
            newThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

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
        return commonMap.get(currentEntry.getKey());
    }

    public MergeDiffInfo<R> mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        return createMergeResult(permissionChecker).executeMerge();
    }

}
