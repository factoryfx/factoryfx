package de.factoryfx.factory.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FactoryLogEntryEvent {

    public final FactoryLogEntryEventType type;
    public final long durationNs;

    @JsonCreator
    public FactoryLogEntryEvent(@JsonProperty("type")FactoryLogEntryEventType type, @JsonProperty("durationNs")long durationNs) {
        this.type = type;
        this.durationNs = durationNs;
    }
}
