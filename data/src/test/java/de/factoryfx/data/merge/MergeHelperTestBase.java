package de.factoryfx.data.merge;

import de.factoryfx.data.Data;

public class MergeHelperTestBase {
    public MergeDiffInfo merge(Data current, Data originalValue, Data newValue){
        MergeResult mergeResult = new MergeResult();
        current.internal().merge(originalValue, newValue, mergeResult,(p)->true);
        MergeDiffInfo mergeDiff = mergeResult.getMergeDiff();
        if (mergeDiff.hasNoConflicts()){
            mergeResult.executeMerge();
        }
        return mergeDiff;
    }
}
