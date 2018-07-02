package de.factoryfx.factory.log;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;

public class FactoryUpdateLog<R extends Data> {
    public final FactoryLogEntryTreeItem root;
    public final Set<FactoryLogEntry> removedFactoryLogs;
    public final MergeDiffInfo<R> mergeDiffInfo;
    /**duration for factory update mostly the time from create/destroy/etc */
    public final long totalDurationNs;
    public final String exception;

    @JsonCreator
    public FactoryUpdateLog(
            @JsonProperty("root")FactoryLogEntryTreeItem factoryLogEntry,
            @JsonProperty("removedFactoryLogs")Set<FactoryLogEntry> removedFactoryLogs,
            @JsonProperty("mergeDiffInfo")MergeDiffInfo<R> mergeDiffInfo,
            @JsonProperty("totalDurationNs")long totalDurationNs,
            @JsonProperty("exception") String exception) {
        this.root = factoryLogEntry;
        this.mergeDiffInfo = mergeDiffInfo;
        this.totalDurationNs = totalDurationNs;
        this.removedFactoryLogs = removedFactoryLogs;
        this.exception=exception;
    }

//    @JsonCreator
    public FactoryUpdateLog(String exception) {
        this.root = null;
        this.mergeDiffInfo = null;
        this.totalDurationNs = 0;
        this.removedFactoryLogs = null;
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
