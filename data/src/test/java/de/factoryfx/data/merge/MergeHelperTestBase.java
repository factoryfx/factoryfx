package de.factoryfx.data.merge;

import de.factoryfx.data.Data;

public class MergeHelperTestBase {
    public MergeDiffInfo merge(Data current, Data originalValue, Data newValue){
        MergeResult mergeResult = new MergeResult(current.internal().copy());
        current.internal().merge(originalValue, newValue, mergeResult,(p)->true);
        return mergeResult.executeMerge();
    }
}
