package io.github.factoryfx.factory.merge;



import io.github.factoryfx.factory.FactoryBase;

import java.util.*;

public class MergeResult<R extends FactoryBase<?,R>> {
    final R previousRoot;
    final R currentRoot;

    final List<AttributeDiffInfo> mergeInfos = new ArrayList<>();
    final List<AttributeDiffInfo> conflictInfos = new ArrayList<>();
    final List<AttributeDiffInfo> mergePermissionViolations = new ArrayList<>();

    final List<Runnable> mergeExecutions = new ArrayList<>();

    final HashMap<UUID, FactoryBase<?, R>> idToFactory;

    public MergeResult(R currentRoot, HashMap<UUID, FactoryBase<?, R>> idToFactory) {
        this.previousRoot = currentRoot.internal().copy();
        this.currentRoot = currentRoot;
        this.idToFactory = idToFactory;
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

            //fix duplicates and priorities old factories to keep there state
            for (FactoryBase<?,R> factory: idToFactory.values()){
                factory.internal().fixDuplicateFactoriesFlat(idToFactory);
            }


            currentRoot.internal().finalise();
//            currentRoot.internal().fixDuplicateFactories();//TODO optimize performance, reuse map from DataMerger
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
