package io.github.factoryfx.factory.merge;


import io.github.factoryfx.factory.FactoryBase;

public class MergeHelperTestBase {
    @SuppressWarnings("unchecked")
    public MergeDiffInfo merge(FactoryBase currentData, FactoryBase commonData, FactoryBase newData){
        return new DataMerger(currentData,commonData,newData).mergeIntoCurrent((p)->true);
    }
}
