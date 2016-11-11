package de.factoryfx.data.merge;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

//JSON serializable
public class MergeDiffInfo {
    public List<MergeResultEntryInfo> mergeInfos;
    public List<MergeResultEntryInfo> conflictInfos;

    public MergeDiffInfo(MergeDiff mergeDiff){
        mergeInfos=mergeDiff.getMergeInfos().stream().map(m->m.createInfo(false)).collect(Collectors.toList());
        conflictInfos=mergeDiff.getConflictInfos().stream().map(m->m.createInfo(true)).collect(Collectors.toList());
    }

    @JsonCreator
    MergeDiffInfo(){

    }

    @JsonIgnore
    public boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }

}
