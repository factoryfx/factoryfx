package de.factoryfx.data.merge;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

//JSON serializable
public class MergeDiffInfo {
    public List<MergeResultEntryInfo> mergeInfos;
    public List<MergeResultEntryInfo> conflictInfos;

    public MergeDiffInfo(MergeDiff mergeDiff){
        mergeInfos=mergeDiff.getMergeInfos().stream().map(m->m.mergeResultEntryInfo).collect(Collectors.toList());
        conflictInfos=mergeDiff.getConflictInfos().stream().map(m->m.mergeResultEntryInfo).collect(Collectors.toList());
    }

    @JsonCreator
    MergeDiffInfo(){

    }

}
