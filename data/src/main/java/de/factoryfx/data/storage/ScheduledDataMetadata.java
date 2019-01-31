package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.GeneralStorageFormat;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScheduledDataMetadata<T> extends StoredDataMetadata<T> implements Delayed{

    /** for scheduled update: date and time of planned activation of this configuration */
    public final LocalDateTime scheduled;

    @JsonCreator
    public ScheduledDataMetadata(
            @JsonProperty("creationTime")LocalDateTime creationTime,
            @JsonProperty("id")String id,
            @JsonProperty("user")String user,
            @JsonProperty("comment")String comment,
            @JsonProperty("baseVersionId")String baseVersionId,
            @JsonProperty("changeSummary")T changeSummary,
            @JsonProperty("storageFormat") GeneralStorageFormat generalStorageFormat,
            @JsonProperty("dataStorageMetadataDictionary") DataStorageMetadataDictionary dataStorageMetadataDictionary,
            @JsonProperty("scheduled")LocalDateTime scheduled) {
        super(creationTime, id, user, comment, baseVersionId, changeSummary, generalStorageFormat, dataStorageMetadataDictionary);
        this.scheduled=scheduled;
    }


    public ScheduledDataMetadata(NewDataMetadata storedDataMetadata, LocalDateTime scheduled, GeneralStorageFormat generalStorageFormat, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this(null, "", "", "", storedDataMetadata.baseVersionId, null, generalStorageFormat,dataStorageMetadataDictionary,scheduled);
    }

    @JsonIgnore
    @Override
    public long getDelay(TimeUnit unit) {
        return convert(unit).between(LocalDateTime.now(),scheduled);
    }

    @JsonIgnore
    @Override
    public int compareTo(Delayed o) {
        return scheduled.compareTo(((ScheduledDataMetadata)o).scheduled);
    }


    private ChronoUnit convert(TimeUnit unit) {
        //TODO if java 9 replace with TimeUnit.toChronoUnit()
        if (unit == null) {
            return null;
        }
        switch (unit) {
            case DAYS:
                return ChronoUnit.DAYS;
            case HOURS:
                return ChronoUnit.HOURS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            default:
                return null;
        }
    }
}
