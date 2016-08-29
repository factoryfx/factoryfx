package de.factoryfx.factory.merge;

import java.util.Locale;
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
        return createMergeResult(currentModel.collectChildFactoriesMap()).getMergeDiff();
    }

    Locale locale=Locale.ENGLISH;
    public FactoryMerger setLocale(Locale locale){
        this.locale=locale;
        return this;
    }

    @SuppressWarnings("unchecked")
    private MergeResult createMergeResult(Map<String, FactoryBase<?,?>> currentMap) {
        MergeResult mergeResult = new MergeResult();

        Map<String, FactoryBase<?,?>> originalMap = originalModel.collectChildFactoriesMap();
        Map<String, FactoryBase<?,?>> newMap = newModel.collectChildFactoriesMap();

        for (Map.Entry<String, FactoryBase<?,?>> entry : currentMap.entrySet()) {
            FactoryBase originalValue = originalMap.get(entry.getKey());
            FactoryBase newValue = newMap.get(entry.getKey());

            entry.getValue().merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult,locale);
        }
        return mergeResult;
    }

    public MergeDiff mergeIntoCurrent() {
        Map<String, FactoryBase<?,?>> currentMap = currentModel.collectChildFactoriesMap();
        MergeResult mergeResult = createMergeResult(currentMap);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts()) {
            mergeResult.executeMerge();
            currentModel.fixDuplicateObjects(s -> Optional.ofNullable(currentMap.get(s)));
        }
        return mergeDiff;
    }
}
