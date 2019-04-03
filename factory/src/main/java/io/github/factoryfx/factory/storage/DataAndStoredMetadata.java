package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.factoryfx.factory.FactoryBase;


import java.time.LocalDateTime;

public class DataAndStoredMetadata<T extends FactoryBase<?,?>,S> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, property="@class")
    public final T root;
    public final StoredDataMetadata<S> metadata;

    @JsonCreator
    public DataAndStoredMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredDataMetadata<S> metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
