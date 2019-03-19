package io.github.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.factoryfx.data.Data;

import java.time.LocalDateTime;

public class DataAndStoredMetadata<T extends Data,S> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, property="@class")
    public final T root;
    public final StoredDataMetadata<S> metadata;

    @JsonCreator
    public DataAndStoredMetadata(@JsonProperty("root") T root, @JsonProperty("metadata")StoredDataMetadata<S> metadata) {
        this.root = root;
        this.metadata = metadata;
    }

    public DataAndStoredMetadata(String user, String comment, DataAndStoredMetadata<T,S> base) {
        this.root = base.root;
        this.metadata = new StoredDataMetadata<>(LocalDateTime.now(),
                base.metadata.id,
                user,
                comment,
                base.metadata.baseVersionId,
                null,
                base.metadata.dataStorageMetadataDictionary
        );
    }
}
