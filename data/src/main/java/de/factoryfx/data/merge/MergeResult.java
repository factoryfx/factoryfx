package de.factoryfx.data.merge;

import de.factoryfx.data.Data;

import java.util.ArrayList;
import java.util.List;

public class MergeResult {
    final Data previousRoot;
    final Data newRoot;

    final List<AttributeDiffInfo> mergeInfos = new ArrayList<>();
    final List<AttributeDiffInfo> conflictInfos = new ArrayList<>();
    final List<AttributeDiffInfo> mergePermissionViolations = new ArrayList<>();

    final List<Runnable> mergeExecutions = new ArrayList<>();

    public MergeResult(Data previousRoot, Data newRoot) {
        this.previousRoot = previousRoot;
        this.newRoot = newRoot;
    }

    public void addConflictInfo(AttributeDiffInfo conflictInfo) {
        conflictInfos.add(conflictInfo);
    }

    public void addMergeExecutions(Runnable mergeAction) {
        mergeExecutions.add(mergeAction);
    }

    public void addMergeInfo(AttributeDiffInfo mergeInfo) {
        mergeInfos.add(mergeInfo);
    }

    public void addPermissionViolationInfo(AttributeDiffInfo permissionViolation) {
        mergePermissionViolations.add(permissionViolation);
    }

    public void executeMerge() {
        for (Runnable mergeAction : mergeExecutions) {
            mergeAction.run();
        }
    }

    public MergeDiffInfo getMergeDiff() {
        return new MergeDiffInfo(mergeInfos, conflictInfos, mergePermissionViolations,previousRoot,newRoot);
    }

}
