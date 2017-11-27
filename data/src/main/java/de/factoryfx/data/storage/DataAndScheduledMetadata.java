package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;

public class DataAndScheduledMetadata<R extends Data> {
    public final R root;
    public final ScheduledDataMetadata metadata;

    @JsonCreator
    public DataAndScheduledMetadata(@JsonProperty("root") R root, @JsonProperty("metadata")ScheduledDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
