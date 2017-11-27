package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;

public class DataAndNewMetadata<T extends Data> {
    public final T root;
    public final NewDataMetadata metadata;

    @JsonCreator
    public DataAndNewMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")NewDataMetadata metadata) {
        this.root = root;
        this.metadata = metadata;
    }

}
