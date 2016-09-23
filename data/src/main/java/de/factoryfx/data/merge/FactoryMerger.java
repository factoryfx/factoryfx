package de.factoryfx.data.merge;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import de.factoryfx.data.Data;

public class FactoryMerger {

    private final Data currentModel;
    private final Data originalModel;
    private final Data newModel;

    public FactoryMerger(Data currentFactory, Data commonFactory, Data newFactory) {
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
    private MergeResult createMergeResult(Map<Object, Data> currentMap) {
        MergeResult mergeResult = new MergeResult();

        Map<Object, Data> originalMap = originalModel.collectChildFactoriesMap();
        Map<Object, Data> newMap = newModel.collectChildFactoriesMap();

        for (Map.Entry<Object, Data> entry : currentMap.entrySet()) {
            Data originalValue = originalMap.get(entry.getKey());
            Data newValue = newMap.get(entry.getKey());

            entry.getValue().merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult,locale);
        }
        return mergeResult;
    }

    public MergeDiff mergeIntoCurrent() {
        Map<Object, Data> currentMap = currentModel.collectChildFactoriesMap();
        MergeResult mergeResult = createMergeResult(currentMap);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();

        if (mergeDiff.hasNoConflicts()) {
            mergeResult.executeMerge();
            currentModel.fixDuplicateObjects(s -> Optional.ofNullable(currentMap.get(s)));
        }
        return mergeDiff;
    }
}
