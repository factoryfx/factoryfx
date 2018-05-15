package de.factoryfx.factory.log;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;

public class FactoryUpdateLog<R extends Data> {
    public final FactoryLogEntry root;
    public final Set<FactoryLogEntry> removedFactoryLogs;
    public final MergeDiffInfo<R> mergeDiffInfo;
    /**duration for factory update mostly the time from create/destroy/etc */
    public final long totalDurationNs;

    @JsonCreator
    public FactoryUpdateLog(
            @JsonProperty("root")FactoryLogEntry factoryLogEntry,
            @JsonProperty("removedFactoryLogs")Set<FactoryLogEntry> removedFactoryLogs,
            @JsonProperty("mergeDiffInfo")MergeDiffInfo<R> mergeDiffInfo,
            @JsonProperty("totalDurationNs")long totalDurationNs) {
        this.root = factoryLogEntry;
        this.mergeDiffInfo = mergeDiffInfo;
        this.totalDurationNs = totalDurationNs;
        this.removedFactoryLogs = removedFactoryLogs;
    }
}
