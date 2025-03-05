package io.github.factoryfx.factory.storage;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.github.factoryfx.factory.FactoryBase;

/**
 * data and metadata for a data update
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataUpdate<R extends FactoryBase<?, ?>> {
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    public R root;
    public String user;
    public String comment;

    /** the base version on the server */
    public final String baseVersionId;

    @JsonIgnore
    public Function<String, Boolean> permissionChecker = (p) -> true;

    @JsonCreator
    public DataUpdate(
        @JsonProperty("root") R root,
        @JsonProperty("user") String user,
        @JsonProperty("comment") String comment,
        @JsonProperty("baseVersionId") String baseVersionId) {
        this.root = root;
        this.user = user;
        this.comment = comment;
        this.baseVersionId = baseVersionId;
    }

    public static <R extends FactoryBase<?, ?>> DataUpdate<R> replaceRoot(DataUpdate<R> dataUpdate, R root) {
        return new DataUpdate<>(root, dataUpdate.user, dataUpdate.comment, dataUpdate.baseVersionId);
    }

    public StoredDataMetadata createUpdateStoredDataMetadata(UpdateSummary changeSummary, String mergerVersionId) {
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

