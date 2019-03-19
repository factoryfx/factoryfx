package io.github.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.factoryfx.data.Data;

public class DataAndId<T extends Data> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, property="@class")
    public final T root;
    public final String id;

    @JsonCreator
    public DataAndId(@JsonProperty("root") T root, @JsonProperty("id")String id) {
        this.root = root;
        this.id = id;
    }
}
