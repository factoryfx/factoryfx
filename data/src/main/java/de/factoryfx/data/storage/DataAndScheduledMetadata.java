package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;

public class DataAndScheduledMetadata<R extends Data> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final R root;
    public final ScheduledDataMetadata metadata;

    @JsonCreator
    public DataAndScheduledMetadata(@JsonProperty("root") R root, @JsonProperty("metadata")ScheduledDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
