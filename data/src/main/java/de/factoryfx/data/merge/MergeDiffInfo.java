package de.factoryfx.data.merge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

//JSON serializable
public class MergeDiffInfo {

    private final String previousRoot;
    private final String newRoot;
    private final Class<? extends Data> rootClazz;

    public final List<AttributeDiffInfo> mergeInfos;
    public final List<AttributeDiffInfo> conflictInfos;
    public final List<AttributeDiffInfo> permissionViolations;

    @JsonCreator
    public MergeDiffInfo(
            @JsonProperty("mergeInfos")List<AttributeDiffInfo> mergeInfos,
            @JsonProperty("conflictInfos")List<AttributeDiffInfo> conflictInfos,
            @JsonProperty("permissionViolations")List<AttributeDiffInfo> permissionViolations,
            @JsonProperty("previousRoot")String previousRoot,
            @JsonProperty("newRoot")String newRoot,
            @JsonProperty("rootClazz")Class<? extends Data> rootClazz){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
        this.previousRoot=previousRoot;
        this.newRoot=newRoot;
        this.rootClazz=rootClazz;
    }

    public MergeDiffInfo(
            List<AttributeDiffInfo> mergeInfos,
            List<AttributeDiffInfo> conflictInfos,
            List<AttributeDiffInfo> permissionViolations,
            Data previousRoot,
            Data newRoot){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
        this.previousRoot=ObjectMapperBuilder.build().writeValueAsString(previousRoot);
        this.newRoot=ObjectMapperBuilder.build().writeValueAsString(newRoot);
        rootClazz=previousRoot.getClass();
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

    @JsonIgnore
    public Data getPreviousRootData() {
        return ObjectMapperBuilder.build().readValue(previousRoot,rootClazz);
    }

    @JsonIgnore
    public Data getNewRootData() {
        return ObjectMapperBuilder.build().readValue(newRoot,rootClazz);
    }
}
