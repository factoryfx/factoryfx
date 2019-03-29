package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.FactoryBase;

import java.util.Map;
import java.util.function.Function;



public class DataMerger<R extends FactoryBase<?,R>> {

    private final R currentData;
    private final R commonData;
    private final R newData;

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
    }

    @SuppressWarnings("unchecked")
    public MergeResult<R> createMergeResult(Function<String,Boolean> permissionChecker) {
        MergeResult mergeResult = new MergeResult(currentData);

        Map<String, FactoryBase<?,R>> currentMap = currentData.internal().collectChildDataMap();
        Map<String, FactoryBase<?,R>> originalMap = commonData.internal().collectChildDataMap();
        Map<String, FactoryBase<?,R>> newMap = newData.internal().collectChildDataMap();

        for (FactoryBase<?,R> newData : newMap.values()) {//avoid mix up with iteration counters
            newData.internal().resetIterationCounterFlat();
        }

        for (Map.Entry<String, FactoryBase<?,R>> entry : currentMap.entrySet()) {
            FactoryBase originalValue = getOriginalValue(originalMap, entry);
            FactoryBase newValue = getNewValue(newMap, entry);

            if (newValue==null && originalValue!=null){
                //check for conflict for removed object
                entry.getValue().internal().visitAttributesDualFlat(originalValue, (name, currentAttribute, originalAttribute) -> {
                    if (!currentAttribute.internal_ignoreForMerging()){
                        if (!currentAttribute.internal_mergeMatch(originalAttribute)){
                            mergeResult.addConflictInfo(new AttributeDiffInfo(name,entry.getValue().getId()));
                        }
                    }
                });
            }

            if (originalValue!=null && newValue!=null){
                entry.getValue().internal().merge(originalValue, newValue, mergeResult, permissionChecker);
            }
        }
        return mergeResult;
    }

    private FactoryBase<?,R> getNewValue(Map<String, FactoryBase<?,R>> newMap, Map.Entry<String, FactoryBase<?,R>> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return newData;
        }
        return newMap.get(currentEntry.getKey());
    }

    private FactoryBase<?,R> getOriginalValue(Map<String, FactoryBase<?,R>> originalMap, Map.Entry<String, FactoryBase<?,R>> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return commonData;
        }
        return originalMap.get(currentEntry.getKey());
    }

    public MergeDiffInfo<R> mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        return createMergeResult(permissionChecker).executeMerge();
    }


}
