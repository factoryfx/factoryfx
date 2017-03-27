package de.factoryfx.data.merge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.factoryfx.data.Data;

public class MergeResult {
    final List<MergeResultEntry> mergeInfos = new ArrayList<>();
    final List<MergeResultEntry> conflictInfos = new ArrayList<>();
    final List<Runnable> mergeExecutions = new ArrayList<>();
    final Set<Data> changedData = new HashSet<>();

    public void addChangedData(Data data) {
        changedData.add(data);
    }

    public void addConflictInfos(MergeResultEntry conflictInfo) {
        conflictInfos.add(conflictInfo);
    }

    public void addMergeExecutions(Runnable mergeAction) {
        mergeExecutions.add(mergeAction);
    }

    public void addMergeInfo(MergeResultEntry mergeInfo) {
        mergeInfos.add(mergeInfo);
    }

    public List<MergeResultEntry> allResults() {
        ArrayList<MergeResultEntry> mergeResultEntries = new ArrayList<>();
        mergeResultEntries.addAll(mergeInfos);
        mergeResultEntries.addAll(conflictInfos);
        return mergeResultEntries;
    }

    public void executeMerge() {
        for (Runnable mergeAction : mergeExecutions) {
            mergeAction.run();
        }
    }

    public MergeDiff getMergeDiff() {
        return new MergeDiff(mergeInfos, conflictInfos, changedData);
    }

}
