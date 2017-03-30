package de.factoryfx.data.merge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

//JSON serializable
public class MergeDiffInfo {
    public final List<AttributeDiffInfo> mergeInfos;
    public final List<AttributeDiffInfo> conflictInfos;
    public final List<AttributeDiffInfo> permissionViolations;

    @JsonCreator
    public MergeDiffInfo(
            @JsonProperty("mergeInfos")List<AttributeDiffInfo> mergeInfos,
            @JsonProperty("conflictInfos")List<AttributeDiffInfo> conflictInfos,
            @JsonProperty("permissionViolations")List<AttributeDiffInfo> permissionViolations){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
    }

    @JsonIgnore
    public int getConflictCount() {
        return conflictInfos.size();
    }

    @JsonIgnore
    public boolean hasNoConflicts() {
        return conflictInfos.isEmpty();
    }

    @JsonIgnore
    public boolean hasNoPermissionViolation() {
        return permissionViolations.isEmpty();
    }

    @JsonIgnore
    public boolean successfullyMerged() {
        return hasNoConflicts() && hasNoPermissionViolation();
    }
}
