package de.factoryfx.data.merge;

import java.util.List;
import java.util.Set;

import de.factoryfx.data.Data;

public class MergeDiff {
    final List<MergeResultEntry> mergeInfos;
    final List<MergeResultEntry> conflictInfos;
    final Set<Data> changedData;

    public MergeDiff(List<MergeResultEntry> mergeInfos, List<MergeResultEntry> conflictInfos, Set<Data> changedData) {
        this.mergeInfos = mergeInfos;
        this.conflictInfos = conflictInfos;
        this.changedData = changedData;

    }

    public int getConflictCount() {
        return conflictInfos.size();
    }

    public List<MergeResultEntry> getConflictInfos() {
        return conflictInfos;
    }

    public List<MergeResultEntry> getMergeInfos() {
        return mergeInfos;
    }

    public boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }

    public Set<Data> getChangedData(){
        return changedData;
    }
}
