package io.github.factoryfx.factory.merge;



import io.github.factoryfx.factory.FactoryBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeResult<R extends FactoryBase<?,R>> {
    final R previousRoot;
    final R currentRoot;

    final List<AttributeDiffInfo> mergeInfos = new ArrayList<>();
    final List<AttributeDiffInfo> conflictInfos = new ArrayList<>();
    final List<AttributeDiffInfo> mergePermissionViolations = new ArrayList<>();

    final List<Runnable> mergeExecutions = new ArrayList<>();

    public MergeResult(R currentRoot) {
        this.previousRoot = currentRoot.internal().copy();
        this.currentRoot = currentRoot;
    }

    public void addConflictInfo(AttributeDiffInfo conflictInfo) {
        conflictInfos.add(conflictInfo);
    }

    private HashSet<FactoryBase<?,R>> mergedFactories= new HashSet<>();
    public void addMergeExecutions(Runnable mergeAction, FactoryBase<?,R> mergeTarget) {
        mergeExecutions.add(mergeAction);
        mergedFactories.add(mergeTarget);
    }

    public void addMergeInfo(AttributeDiffInfo mergeInfo) {
        mergeInfos.add(mergeInfo);
    }

    public void addPermissionViolationInfo(AttributeDiffInfo permissionViolation) {
        mergePermissionViolations.add(permissionViolation);
    }

    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> executeMerge() {
        if (hasNoConflicts() && hasNoPermissionViolation()){
            for (Runnable mergeAction : mergeExecutions) {
                mergeAction.run();
            }
            currentRoot.internal().needRecalculationForBackReferences();
            currentRoot.internal().finalise();
            currentRoot.internal().fixDuplicateFactories();
        }
        return new MergeDiffInfo<>(mergeInfos, conflictInfos, mergePermissionViolations, previousRoot, currentRoot, (Class<R>) currentRoot.getClass());

    }

    private boolean hasNoPermissionViolation() {
        return mergePermissionViolations.isEmpty();
    }

    private boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }

    public Set<FactoryBase<?,R>> getMergedFactories(){
        return mergedFactories;
    }

}
