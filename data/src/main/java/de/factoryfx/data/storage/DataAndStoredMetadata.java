package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;

public class DataAndStoredMetadata<T extends Data> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final T root;
    public final StoredDataMetadata metadata;

    @JsonCreator
    public DataAndStoredMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
