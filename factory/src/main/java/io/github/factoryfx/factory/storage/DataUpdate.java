package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.annotation.*;
import io.github.factoryfx.factory.FactoryBase;


import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

/**
 * data and metadata for a data update
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataUpdate<R extends FactoryBase<?,?>> {
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public final R root;
    public String user;
    public String comment;

    /**the base version on the server*/
    public final String baseVersionId;

    @JsonIgnore
    public Function<String,Boolean> permissionChecker=(p)->true;

    @JsonCreator
    public DataUpdate(
            @JsonProperty("root")R root,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId) {
        this.root = root;
        this.user = user;
        this.comment = comment;
        this.baseVersionId = baseVersionId;
    }

    public StoredDataMetadata createUpdateStoredDataMetadata(UpdateSummary changeSummary, String mergerVersionId){
        return new StoredDataMetadata(
            LocalDateTime.now(),
            UUID.randomUUID().toString(),
            this.user,
            this.comment,
            this.baseVersionId,
            changeSummary,
            root.internal().createDataStorageMetadataDictionaryFromRoot(),
            mergerVersionId
        );
    }
}

