package de.factoryfx.factory.merge;

import java.util.List;

public class MergeDiff {
    final List<MergeResultEntry<?>> mergeInfos;
    final List<MergeResultEntry<?>> conflictInfos;

    public MergeDiff(List<MergeResultEntry<?>> mergeInfos, List<MergeResultEntry<?>> conflictInfos) {
        this.mergeInfos = mergeInfos;
        this.conflictInfos = conflictInfos;
    }

    public int getConflictCount() {
        return conflictInfos.size();
    }

    public List<MergeResultEntry<?>> getConflictInfos() {
        return conflictInfos;
    }

    public List<MergeResultEntry<?>> getMergeInfos() {
        return mergeInfos;
    }

    public boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }
}
