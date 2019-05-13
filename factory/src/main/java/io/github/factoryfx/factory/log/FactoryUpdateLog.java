package io.github.factoryfx.factory.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.merge.MergeDiffInfo;

public class FactoryUpdateLog<R extends FactoryBase<?,?>> {
    public final String log;
    public final MergeDiffInfo<R> mergeDiffInfo;
    /**duration for factory update mostly the time from create/destroy/etc */
    public final long totalDurationNs;
    public final String exception;

    @JsonCreator
    public FactoryUpdateLog(
            @JsonProperty("log")String log,
            @JsonProperty("mergeDiffInfo")MergeDiffInfo<R> mergeDiffInfo,
            @JsonProperty("totalDurationNs")long totalDurationNs,
            @JsonProperty("exception") String exception) {
        this.log = log;
        this.mergeDiffInfo = mergeDiffInfo;
        this.totalDurationNs = totalDurationNs;
        this.exception=exception;
    }

//    @JsonCreator
    public FactoryUpdateLog(String exception) {
        this.log = "";
        this.mergeDiffInfo = null;
        this.totalDurationNs = 0;
        this.exception = exception;
    }

    public boolean successfullyMerged() {
        if (mergeDiffInfo!=null){
            return mergeDiffInfo.successfullyMerged();
        }
        return false;
    }

    public boolean failedUpdate() {
        return exception!=null;
    }
}
