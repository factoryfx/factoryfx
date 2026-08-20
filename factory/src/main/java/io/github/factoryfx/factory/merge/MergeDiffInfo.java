package io.github.factoryfx.factory.merge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

/**
 * @param <R> root data type
 */
public class MergeDiffInfo<R extends FactoryBase<?,?>> {
    @JsonProperty
    private final Class<R> rootClazz;//used from jackson

    public final List<AttributeDiffInfo> mergeInfos;
    public final List<AttributeDiffInfo> conflictInfos;
    public final List<AttributeDiffInfo> permissionViolations;
    /** server validation errors, reported by simulateUpdate; does not affect {@link #successfullyMerged()} */
    public final List<String> validationErrors;

    @JsonIgnore
    private final R previousRoot;
    @JsonIgnore
    private final R newRoot;

    @JsonCreator
    public MergeDiffInfo(
            @JsonProperty("mergeInfos")List<AttributeDiffInfo> mergeInfos,
            @JsonProperty("conflictInfos")List<AttributeDiffInfo> conflictInfos,
            @JsonProperty("permissionViolations")List<AttributeDiffInfo> permissionViolations,
            @JsonProperty("validationErrors")List<String> validationErrors,
            @JsonProperty("previousRoot")String previousRoot,
            @JsonProperty("newRoot")String newRoot,
            @JsonProperty("rootClazz")Class<R> rootClazz){
        this.mergeInfos=mergeInfos;
        this.conflictInfos=conflictInfos;
        this.permissionViolations = permissionViolations;
        this.validationErrors = validationErrors == null ? List.of() : validationErrors;
        this.previousRoot= ObjectMapperBuilder.build().readValue(previousRoot,rootClazz);
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
        this.validationErrors = List.of();
        this.previousRoot=previousRoot;
        this.newRoot=newRoot;
        this.rootClazz = rootClazz;
    }

    /**
     * copy with server validation errors attached
     * @param base base
     * @param validationErrors server validation errors
     */
    public MergeDiffInfo(MergeDiffInfo<R> base, List<String> validationErrors){
        this.mergeInfos=base.mergeInfos;
        this.conflictInfos=base.conflictInfos;
        this.permissionViolations=base.permissionViolations;
        this.validationErrors=validationErrors;
        this.previousRoot=base.previousRoot;
        this.newRoot=base.newRoot;
        this.rootClazz=base.rootClazz;
    }

    //TODO refactor to use JsonNode instead of string
    public String getPreviousRoot() {//workaround for duplicated ids
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
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
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
