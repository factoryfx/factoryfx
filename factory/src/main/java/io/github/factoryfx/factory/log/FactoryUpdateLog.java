package io.github.factoryfx.factory.log;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.merge.MergeDiffInfo;

public class FactoryUpdateLog<R extends FactoryBase<?, ?>> {
    public final String log;
    public final MergeDiffInfo<R> mergeDiffInfo;
    /** duration for factory update mostly the time from create/destroy/etc */
    public final long totalDurationNs;
    public final String exception;
    /** server validation errors that rejected the update, empty if the update was not rejected by validation */
    public final List<String> validationErrors;

    @JsonCreator
    public FactoryUpdateLog(
        @JsonProperty("log") String log,
        @JsonProperty("mergeDiffInfo") MergeDiffInfo<R> mergeDiffInfo,
        @JsonProperty("totalDurationNs") long totalDurationNs,
        @JsonProperty("exception") String exception,
        @JsonProperty("validationErrors") List<String> validationErrors) {
        this.log = log;
        this.mergeDiffInfo = mergeDiffInfo;
        this.totalDurationNs = totalDurationNs;
        this.exception = exception;
        this.validationErrors = validationErrors == null ? List.of() : validationErrors;
    }

    public FactoryUpdateLog(String log, MergeDiffInfo<R> mergeDiffInfo, long totalDurationNs, String exception) {
        this(log, mergeDiffInfo, totalDurationNs, exception, List.of());
    }

    public FactoryUpdateLog(String exception) {
        this.log = "";
        this.mergeDiffInfo = null;
        this.totalDurationNs = 0;
        this.exception = exception;
        this.validationErrors = List.of();
    }

    /**
     * update rejected by server validation, see {@link io.github.factoryfx.factory.attribute.Attribute#serverValidation}
     * @param validationErrors validation errors
     * @param <R> root
     * @return log with {@link #failedValidation()} true
     */
    public static <R extends FactoryBase<?, ?>> FactoryUpdateLog<R> validationFailed(List<String> validationErrors) {
        return new FactoryUpdateLog<>("", null, 0, null, validationErrors);
    }

    public boolean successfullyMerged() {
        if (mergeDiffInfo != null) {
            return mergeDiffInfo.successfullyMerged();
        }
        return false;
    }

    public boolean failedUpdate() {
        return exception != null;
    }

    /**
     * @return true if the update was rejected by server validation, nothing was applied or persisted
     */
    public boolean failedValidation() {
        return !validationErrors.isEmpty();
    }

    public void dumpError(Consumer<String> receiver) {
        if (!this.successfullyMerged() && this.mergeDiffInfo != null) {
            receiver.accept(this.mergeDiffInfo.mergeInfos.stream().map(o -> o.attributeName).collect(Collectors.joining(", ", "mergeInfos: ", "")));
            if (!this.mergeDiffInfo.hasNoConflicts()) {
                receiver.accept(this.mergeDiffInfo.conflictInfos.stream().map(o -> o.attributeName).collect(Collectors.joining(", ", "conflict fields: ", "")));
            }

            if (!this.mergeDiffInfo.hasNoPermissionViolation()) {
                receiver.accept(this.mergeDiffInfo.permissionViolations.stream().map(pv -> pv.attributeName).collect(Collectors.joining(", ", "conflict permissions: ", "")));
            }
        }
        for (String validationError : validationErrors) {
            receiver.accept("validation error:\n" + validationError);
        }
        if (this.failedUpdate()) {
            receiver.accept(this.exception);
        }
    }
}
