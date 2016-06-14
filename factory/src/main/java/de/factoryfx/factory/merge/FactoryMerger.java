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

    public MergeDiff createMergeResult() {
        return createMergeResult(currentModel.collectModelEntitiesMap()).getMergeDiff();
    }

    @SuppressWarnings("unchecked")
    private MergeResult createMergeResult(Map<String, FactoryBase<?,?>> currentMap) {
        MergeResult mergeResult = new MergeResult();

        Map<String, FactoryBase<?,?>> originalMap = originalModel.collectModelEntitiesMap();
        Map<String, FactoryBase<?,?>> newMap = newModel.collectModelEntitiesMap();

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
//TODO
//            mergeResultEntry.setPath(currentModel.getMassPathTo(childToParentMap, mergeResultEntry.getPreviousEntityModel()));
        }
        return mergeResult;
    }

    public MergeDiff mergeIntoCurrent() {
        Map<String, FactoryBase<?,?>> currentMap = currentModel.collectModelEntitiesMap();
        MergeResult mergeResult = createMergeResult(currentMap);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts()) {

            for (FactoryBase<?,?> current : currentMap.values()){
                current.unMarkChanged();
            }

            for (MergeResultEntry<?> mergeResultEntry: mergeDiff.getMergeInfos()){
                mergeResultEntry.parent.markChanged();
            }

            mergeResult.executeMerge();
            currentModel.fixDuplicateObjects(s -> Optional.ofNullable(currentMap.get(s)));
        }
        return mergeDiff;
    }
}
