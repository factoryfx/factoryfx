package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.factoryfx.factory.FactoryBase;

public class DataAndStoredMetadata<T extends FactoryBase<?,?>> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    public final T root;
    public final StoredDataMetadata metadata;

    @JsonCreator
    public DataAndStoredMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }
}
