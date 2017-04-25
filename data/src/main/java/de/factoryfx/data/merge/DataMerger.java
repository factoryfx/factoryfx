package de.factoryfx.data.merge;

import java.util.Map;
import java.util.function.Function;

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

    public MergeDiffInfo createMergeResult(Function<String,Boolean> permissionChecker) {
        return createMergeResult(currentModel.internal().collectChildFactoriesMap(),permissionChecker).getMergeDiff();
    }

    @SuppressWarnings("unchecked")
    private MergeResult createMergeResult(Map<String, Data> currentMap, Function<String,Boolean> permissionChecker) {
        MergeResult mergeResult = new MergeResult();

        Map<String, Data> originalMap = originalModel.internal().collectChildFactoriesMap();
        Map<String, Data> newMap = newModel.internal().collectChildFactoriesMap();

        for (Map.Entry<String, Data> entry : currentMap.entrySet()) {
            Data originalValue = originalMap.get(entry.getKey());
            Data newValue = newMap.get(entry.getKey());

            if (newValue==null && originalValue!=null){
                //check for conflict for removed object
                entry.getValue().internal().visitAttributesDualFlat(originalValue, (currentAttribute, originalAttribute) -> {
                    if (!currentAttribute.internal_match(originalAttribute)){
                        mergeResult.addConflictInfo(new AttributeDiffInfo(entry.getValue(),currentAttribute));
                    }
                });
            }

            if (originalValue!=null && newValue!=null){
                entry.getValue().internal().merge(originalValue, newValue, mergeResult, permissionChecker);
            }
        }
        return mergeResult;
    }

    public MergeDiffInfo mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        Map<String, Data> currentMap = currentModel.internal().collectChildFactoriesMap();
        MergeResult mergeResult = createMergeResult(currentMap,permissionChecker);
        MergeDiffInfo mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts() && mergeDiff.hasNoPermissionViolation()) {
            mergeResult.executeMerge();
            currentModel.internal().fixDuplicateObjects();
        }
        return mergeDiff;
    }
}
