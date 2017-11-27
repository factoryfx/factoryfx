package de.factoryfx.data.merge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

//JSON serializable
public class MergeDiffInfo<R extends Data> {
    @JsonProperty
    private final Class<R> rootClazz;

    public final List<AttributeDiffInfo> mergeInfos;
    public final List<AttributeDiffInfo> conflictInfos;
    public final List<AttributeDiffInfo> permissionViolations;

    @JsonIgnore
    private final R previousRoot;
    @JsonIgnore
    private final R newRoot;

    @JsonCreator
    public MergeDiffInfo(
            @JsonProperty("mergeInfos")List<AttributeDiffInfo> mergeInfos,
            @JsonProperty("conflictInfos")List<AttributeDiffInfo> conflictInfos,
            @JsonProperty("permissionViolations")List<AttributeDiffInfo> permissionViolations,
            @JsonProperty("previousRoot")String previousRoot,
            @JsonProperty("newRoot")String newRoot,
            @JsonProperty("rootClazz")Class<R> rootClazz){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
        this.previousRoot=ObjectMapperBuilder.build().readValue(previousRoot,rootClazz);
        this.newRoot=ObjectMapperBuilder.build().readValue(newRoot,rootClazz);
        this.rootClazz=rootClazz;
    }

    public MergeDiffInfo(
            List<AttributeDiffInfo> mergeInfos,
            List<AttributeDiffInfo> conflictInfos,
            List<AttributeDiffInfo> permissionViolations,
            R previousRoot,
            R newRoot,
            Class<R> rootClazz){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
        this.previousRoot=previousRoot;
        this.newRoot=newRoot;
        this.rootClazz = rootClazz;
    }

    public String getPreviousRoot() {
        return ObjectMapperBuilder.build().writeValueAsString(previousRoot);
    }

    public String getNewRoot() {
        return ObjectMapperBuilder.build().writeValueAsString(newRoot);
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
    public R getPreviousRootData() {
        return previousRoot;
    }

    @JsonIgnore
    public R getNewRootData() {
        return newRoot;
    }
}
