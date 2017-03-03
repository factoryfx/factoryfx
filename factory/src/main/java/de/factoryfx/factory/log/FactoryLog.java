package de.factoryfx.factory.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.merge.MergeDiffInfo;

public class FactoryLog {
    public final FactoryLogEntry root;
    public final MergeDiffInfo mergeDiffInfo;

    @JsonCreator
    public FactoryLog(@JsonProperty("root")FactoryLogEntry factoryLogEntry, @JsonProperty("mergeDiffInfo")MergeDiffInfo mergeDiffInfo) {
        this.root = factoryLogEntry;
        this.mergeDiffInfo = mergeDiffInfo;
    }
}
