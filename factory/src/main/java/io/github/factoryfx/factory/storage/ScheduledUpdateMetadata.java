package io.github.factoryfx.factory.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
/**
 * metadata for a future data
 *
 * implements Delayed to make it's easy usable width a DelayQueue
 *
 * Note: this class has a natural ordering that is inconsistent with equals
 */
public class ScheduledUpdateMetadata implements Delayed {
    public final String id;
    /**id for the complete configuration, NOT any factory id*/
    public final String user;
    public final String comment;

    /** for scheduled update: date and time of planned activation of this configuration */
    public final LocalDateTime scheduled;

    public final DataStorageMetadataDictionary dataStorageMetadataDictionary;

    @JsonCreator
    public ScheduledUpdateMetadata(
            @JsonProperty("id")String id,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("scheduled")LocalDateTime scheduled,
            @JsonProperty("dataStorageMetadataDictionary")DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this.id=id;
        this.scheduled=scheduled;
        this.user = user;
        this.comment = comment;
        this.dataStorageMetadataDictionary = dataStorageMetadataDictionary;
    }

    @JsonIgnore
    @Override
    public long getDelay(TimeUnit unit) {
        return convert(unit).between(LocalDateTime.now(),scheduled);
    }

    @JsonIgnore
    @Override
    public int compareTo(Delayed o) {
        return scheduled.compareTo(((ScheduledUpdateMetadata)o).scheduled);
    }

    private ChronoUnit convert(TimeUnit unit) {
        return unit.toChronoUnit();
    }
}
