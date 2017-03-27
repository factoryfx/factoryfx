package de.factoryfx.data.merge;

import java.util.Map;
import java.util.Optional;

import de.factoryfx.data.Data;

public class DataMerger {

    private final Data currentModel;
    private final Data originalModel;
    private final Data newModel;

    public DataMerger(Data currentFactory, Data commonFactory, Data newFactory) {
        this.currentModel = currentFactory;
        this.originalModel = commonFactory;
        this.newModel = newFactory;
    }

    public MergeDiff createMergeResult() {
        return createMergeResult(currentModel.internal().collectChildFactoriesMap()).getMergeDiff();
    }


    @SuppressWarnings("unchecked")
    private MergeResult createMergeResult(Map<String, Data> currentMap) {
        MergeResult mergeResult = new MergeResult();

        Map<String, Data> originalMap = originalModel.internal().collectChildFactoriesMap();
        Map<String, Data> newMap = newModel.internal().collectChildFactoriesMap();

        for (Map.Entry<String, Data> entry : currentMap.entrySet()) {
            Data originalValue = originalMap.get(entry.getKey());
            Data newValue = newMap.get(entry.getKey());

            entry.getValue().internal().merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult);
        }
        return mergeResult;
    }

    public MergeDiff mergeIntoCurrent() {
        Map<String, Data> currentMap = currentModel.internal().collectChildFactoriesMap();
        MergeResult mergeResult = createMergeResult(currentMap);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts()) {
            mergeResult.executeMerge();
            currentModel.internal().fixDuplicateObjects();
        }
        return mergeDiff;
    }
}
