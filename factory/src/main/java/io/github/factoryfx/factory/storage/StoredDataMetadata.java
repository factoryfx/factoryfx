package io.github.factoryfx.factory.storage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

/**
 * metadata for a stored historical factory
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(value = { "dataModelVersion" })
public class StoredDataMetadata {
    public final LocalDateTime creationTime;
    /**id for the complete configuration, NOT any factory id*/
    public final String id;
    public final String user;
    public final String comment;

    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final UpdateSummary changeSummary;

    /**the base version on the server*/
    public final String baseVersionId;

    /** the current version used for merging */
    public final String mergerVersionId;

    public final DataStorageMetadataDictionary dataStorageMetadataDictionary;

    /**
     * configuration schema version used for version-gated patches, null means unversioned (treated as 0).<br>
     * intentionally not final: configuration patches may update it, {@link DataStorage#patchAll} persists the mutation
     */
    public Integer configurationSchemaVersion;

    @JsonCreator
    public  StoredDataMetadata(
            @JsonProperty("creationTime")LocalDateTime creationTime,
            @JsonProperty("id")String id,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId,
            @JsonProperty("changeSummary")UpdateSummary changeSummary,
            @JsonProperty("dataStorageMetadataDictionary") DataStorageMetadataDictionary dataStorageMetadataDictionary,
            @JsonProperty("mergerVersionId")String mergerVersionId,
            @JsonProperty("configurationSchemaVersion")Integer configurationSchemaVersion) {
        this.creationTime = creationTime;
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.baseVersionId = baseVersionId;
        this.changeSummary = changeSummary;
        this.dataStorageMetadataDictionary = dataStorageMetadataDictionary;
        this.mergerVersionId = mergerVersionId;
        this.configurationSchemaVersion = configurationSchemaVersion;
    }

    public StoredDataMetadata(
            LocalDateTime creationTime,
            String id,
            String user,
            String comment,
            String baseVersionId,
            UpdateSummary changeSummary,
            DataStorageMetadataDictionary dataStorageMetadataDictionary,
            String mergerVersionId) {
        this(creationTime,id,user,comment,baseVersionId,changeSummary,dataStorageMetadataDictionary,mergerVersionId,null);
    }

    public StoredDataMetadata(String id, String user, String comment, String baseVersionId, UpdateSummary changeSummary, DataStorageMetadataDictionary dataStorageMetadataDictionary,String mergerVersionId) {
        this(LocalDateTime.now(),id,user,comment,baseVersionId,changeSummary,dataStorageMetadataDictionary,mergerVersionId,null);
    }

    public StoredDataMetadata(String id, String user, String comment, String baseVersionId, UpdateSummary changeSummary, DataStorageMetadataDictionary dataStorageMetadataDictionary,String mergerVersionId, Integer configurationSchemaVersion) {
        this(LocalDateTime.now(),id,user,comment,baseVersionId,changeSummary,dataStorageMetadataDictionary,mergerVersionId,configurationSchemaVersion);
    }

    public static StoredDataMetadata createLightStoredDataMetadata(JsonNode root) {
        return new StoredDataMetadata(
                root.hasNonNull("creationTime") ? LocalDateTime.parse(root.get("creationTime").asText()) : null,
                root.path("id").asText(null),
                root.path("user").asText(null),
                root.path("comment").asText(null),
                root.path("baseVersionId").asText(null),
                null,
                null,
                root.path("mergerVersionId").asText(null),
                root.hasNonNull("configurationSchemaVersion") ? root.get("configurationSchemaVersion").asInt() : null);
    }

    public static StoredDataMetadata createLightStoredDataMetadata(StoredDataMetadata storedDataMetadata) {
        return new StoredDataMetadata(storedDataMetadata.creationTime,
                storedDataMetadata.id,
                storedDataMetadata.user,
                storedDataMetadata.comment,
                storedDataMetadata.baseVersionId,
                null,
                null,
                storedDataMetadata.mergerVersionId,
                storedDataMetadata.configurationSchemaVersion);
    }

    @JsonIgnore
    public boolean isInitialFactory() {
        return  mergerVersionId==null;
    }

}
