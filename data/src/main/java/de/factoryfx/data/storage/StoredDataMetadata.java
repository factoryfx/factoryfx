package de.factoryfx.data.storage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * metadata for a stored historical factory
 *
 * @param <S> Summary for this change
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StoredDataMetadata<S> {
    public final LocalDateTime creationTime;
    /**id for the complete configuration, NOT any factory id*/
    public final String id;
    public final String user;
    public final String comment;

    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final S changeSummary;

    /**the base version on the server*/
    public final  String baseVersionId;

    /** version of the factory structure used for migration*/
    public final  int dataModelVersion;

    @JsonCreator
    public StoredDataMetadata(
            @JsonProperty("creationTime")LocalDateTime creationTime,
            @JsonProperty("id")String id,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId,
            @JsonProperty("dataModelVersion")int dataModelVersion,
            @JsonProperty("changeSummary")S changeSummary) {
        this.creationTime = creationTime;
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.changeSummary = changeSummary;
        this.baseVersionId = baseVersionId;
        this.dataModelVersion = dataModelVersion;
    }

    public StoredDataMetadata( String id, String user, String comment, String baseVersionId, int dataModelVersion, S changeSummary) {
        this.creationTime=LocalDateTime.now();
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.changeSummary = changeSummary;
        this.baseVersionId = baseVersionId;
        this.dataModelVersion = dataModelVersion;
    }
}
