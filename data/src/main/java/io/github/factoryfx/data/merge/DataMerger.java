package io.github.factoryfx.data.merge;

import java.util.Map;
import java.util.function.Function;

import io.github.factoryfx.data.Data;

public class DataMerger<R extends Data> {

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

        Map<String, Data> currentMap = currentData.internal().collectChildDataMap();
        Map<String, Data> originalMap = commonData.internal().collectChildDataMap();
        Map<String, Data> newMap = newData.internal().collectChildDataMap();

        for (Data newData : newMap.values()) {//avoid mixup with iteration countern
            newData.internal().resetIterationCounterFlat();
        }

        for (Map.Entry<String, Data> entry : currentMap.entrySet()) {
            Data originalValue = getOriginalValue(originalMap, entry);
            Data newValue = getNewValue(newMap, entry);

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

    private Data getNewValue(Map<String, Data> newMap, Map.Entry<String, Data> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return newData;
        }
        return newMap.get(currentEntry.getKey());
    }

    private Data getOriginalValue(Map<String, Data> originalMap, Map.Entry<String, Data> currentEntry) {
        if (currentEntry.getValue()==currentData){//for root different id don't make sense
            return commonData;
        }
        return originalMap.get(currentEntry.getKey());
    }

    public MergeDiffInfo<R> mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        return createMergeResult(permissionChecker).executeMerge();
    }


}
