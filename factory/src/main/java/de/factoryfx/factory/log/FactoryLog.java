package de.factoryfx.factory.log;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.merge.MergeDiffInfo;

public class FactoryLog {
    public final FactoryLogEntry root;
    public final List<FactoryLogEntry> removedFactoryLogs;
    public final MergeDiffInfo mergeDiffInfo;
    /**duration for factory update mostly the time from create/destory/etc */
    public final long totalDurationNs;

    @JsonCreator
    public FactoryLog(@JsonProperty("root")FactoryLogEntry factoryLogEntry, @JsonProperty("removedFactoryLogs")List<FactoryLogEntry> removedFactoryLogs, @JsonProperty("mergeDiffInfo")MergeDiffInfo mergeDiffInfo, @JsonProperty("totalDurationNs")long totalDurationNs) {
        this.root = factoryLogEntry;
        this.mergeDiffInfo = mergeDiffInfo;
        this.totalDurationNs = totalDurationNs;
        this.removedFactoryLogs = removedFactoryLogs;
    }
}
