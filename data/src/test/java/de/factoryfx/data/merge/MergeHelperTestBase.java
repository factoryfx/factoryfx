package de.factoryfx.data.merge;

import java.util.Optional;

import de.factoryfx.data.Data;

public class MergeHelperTestBase {
    public MergeDiff merge(Data current, Data originalValue, Data newValue){
        MergeResult mergeResult = new MergeResult();
        current.merge(Optional.ofNullable(originalValue), Optional.ofNullable(newValue), mergeResult);
        MergeDiff mergeDiff = mergeResult.getMergeDiff();
        if (mergeDiff.hasNoConflicts()){
            mergeResult.executeMerge();
        }
        return mergeDiff;
    }
}
