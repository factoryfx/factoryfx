package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;

public class MergeHelperTestBase {
    @SuppressWarnings("unchecked")
    public MergeDiffInfo merge(FactoryBase current, FactoryBase originalValue, FactoryBase newValue){
        MergeResult mergeResult = new MergeResult(current.internal().copy());
        current.internal().merge(originalValue, newValue, mergeResult,(p)->true);
        return mergeResult.executeMerge();
    }
}
