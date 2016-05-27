package de.factoryfx.factory.merge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;

public class FactoryMerger {

    private final FactoryBase<?,?> currentModel;
    private final FactoryBase<?,?> originalModel;
    private final FactoryBase<?,?> newModel;

    public FactoryMerger(FactoryBase<?,?> currentFactory, FactoryBase<?,?> commonFactory, FactoryBase<?,?> newFactory) {
        this.currentModel = currentFactory;
        this.originalModel = commonFactory;
        this.newModel = newFactory;
    }

    private HashMap<String, FactoryBase<?,?>> collectFlatMap(FactoryBase<?,?> root) {
        HashMap<String, FactoryBase<?,?>> result = new HashMap<>();
        HashSet<FactoryBase<?,?>> allModelEntities = new HashSet<>();
        root.collectModelEntitiesTo(allModelEntities);

        for (FactoryBase<?,?> base : allModelEntities) {
            result.put(base.getId(), base);
        }
        return result;
    }

    public MergeDiff createMergeResult() {
        return createMergeResult(collectFlatMap(currentModel)).getMergeDiff();
    }

    @SuppressWarnings("unchecked")
    private MergeResult createMergeResult(HashMap<String, FactoryBase<?,?>> currentMap) {
        MergeResult mergeResult = new MergeResult();

        HashMap<String, FactoryBase<?,?>> originalMap = collectFlatMap(originalModel);
        HashMap<String, FactoryBase<?,?>> newMap = collectFlatMap(newModel);

        for (Map.Entry<String, FactoryBase<?,?>> entry : currentMap.entrySet()) {
            FactoryBase originalValue = originalMap.get(entry.getKey());
            FactoryBase newValue = newMap.get(entry.getKey());

            entry.getValue().merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult);
        }

        HashSet<FactoryBase<?,?>> allModelEntities = new HashSet<>();
        for (Map.Entry<String, FactoryBase<?,?>> entry : currentMap.entrySet()) {
            allModelEntities.add(entry.getValue());
        }
        HashMap<FactoryBase<?,?>, FactoryBase<?,?>> childToParentMap = currentModel.getChildToParentMap(allModelEntities);
        for (MergeResultEntry<?> mergeResultEntry : mergeResult.allResults()) {
            mergeResultEntry.setPath(currentModel.getMassPathTo(childToParentMap, mergeResultEntry.getPreviousEntityModel()));
        }
        return mergeResult;
    }

    public MergeDiff mergeIntoCurrent() {
        HashMap<String, FactoryBase<?,?>> currentMap = collectFlatMap(currentModel);
        MergeResult mergeResult = createMergeResult(currentMap);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts()) {
            mergeResult.executeMerge();
        }

        currentModel.fixDuplicateObjects(s -> Optional.ofNullable(currentMap.get(s)));
        return mergeDiff;
    }
}
