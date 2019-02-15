package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.factoryfx.data.Data;

import java.time.LocalDateTime;

public class ScheduledUpdate<R extends Data> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final R root;
    /**id for the complete configuration, NOT any factory id*/
    public final String user;
    public final String comment;

    /**the base version on the server*/
    public final String baseVersionId;

    /** for scheduled update: date and time of planned activation of this configuration */
    public final LocalDateTime scheduled;

    @JsonCreator
    public ScheduledUpdate(
            @JsonProperty("root")R root,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId,
            @JsonProperty("scheduled")LocalDateTime scheduled) {
        this.root=root;
        this.scheduled=scheduled;
        this.user = user;
        this.comment = comment;
        this.baseVersionId = baseVersionId;
    }


}
