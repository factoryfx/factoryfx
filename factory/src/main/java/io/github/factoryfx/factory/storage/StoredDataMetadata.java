package io.github.factoryfx.factory.storage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

/**
 * metadata for a stored historical factory
 *
 * @param <S> Summary for this change
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(value = { "dataModelVersion" })
public class StoredDataMetadata<S> {
    public final LocalDateTime creationTime;
    /**id for the complete configuration, NOT any factory id*/
    public final String id;
    public final String user;
    public final String comment;

    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final S changeSummary;

    /**the base version on the server*/
    public final String baseVersionId;

    public final DataStorageMetadataDictionary dataStorageMetadataDictionary;

    @JsonCreator
    public  StoredDataMetadata(
            @JsonProperty("creationTime")LocalDateTime creationTime,
            @JsonProperty("id")String id,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId,
            @JsonProperty("changeSummary")S changeSummary,
            @JsonProperty("dataStorageMetadataDictionary") DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this.creationTime = creationTime;
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.baseVersionId = baseVersionId;
        this.changeSummary = changeSummary;
        this.dataStorageMetadataDictionary = dataStorageMetadataDictionary;
    }

    public StoredDataMetadata(String id, String user, String comment, String baseVersionId, S changeSummary, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this(LocalDateTime.now(),id,user,comment,baseVersionId,changeSummary,dataStorageMetadataDictionary);
    }
}
