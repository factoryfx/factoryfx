package io.github.factoryfx.data.merge;

import io.github.factoryfx.data.Data;

public class MergeHelperTestBase {
    @SuppressWarnings("unchecked")
    public MergeDiffInfo merge(Data current, Data originalValue, Data newValue){
        MergeResult mergeResult = new MergeResult(current.internal().copy());
        current.internal().merge(originalValue, newValue, mergeResult,(p)->true);
        return mergeResult.executeMerge();
    }
}
