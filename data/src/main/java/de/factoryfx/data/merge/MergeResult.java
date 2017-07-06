package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

import java.util.ArrayList;
import java.util.List;

public class MergeResult {
    final String previousRoot;
    final Data currentRoot;

    final List<AttributeDiffInfo> mergeInfos = new ArrayList<>();
    final List<AttributeDiffInfo> conflictInfos = new ArrayList<>();
    final List<AttributeDiffInfo> mergePermissionViolations = new ArrayList<>();

    final List<Runnable> mergeExecutions = new ArrayList<>();

    public MergeResult(Data currentRoot) {
        this.previousRoot = ObjectMapperBuilder.build().writeValueAsString(currentRoot);
        this.currentRoot = currentRoot;
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

    public MergeDiffInfo executeMerge() {
        if (hasNoConflicts() && hasNoPermissionViolation()){
            for (Runnable mergeAction : mergeExecutions) {
                mergeAction.run();
            }
            currentRoot.internal().fixDuplicateData();
        }
        return new MergeDiffInfo(mergeInfos, conflictInfos, mergePermissionViolations,previousRoot,ObjectMapperBuilder.build().writeValueAsString(currentRoot),currentRoot.getClass());

    }

    private boolean hasNoPermissionViolation() {
        return mergePermissionViolations.isEmpty();
    }

    private boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }

}
