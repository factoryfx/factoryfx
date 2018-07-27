package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;

/**
 * @param <R> root data type
 */
public class DataAndNewMetadata<R extends Data> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final R root;
    public final NewDataMetadata metadata;

    @JsonCreator
    public DataAndNewMetadata(@JsonProperty("root") R root, @JsonProperty("metadata")NewDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

}
