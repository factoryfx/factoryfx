package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * metadata for a future data
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScheduledDataMetadata extends StoredDataMetadata implements Delayed{

    /** for scheduled update: date and time of planned activation of this configuration */
    public LocalDateTime scheduled;

    @JsonCreator
    public ScheduledDataMetadata() {
        super();
    }


    public ScheduledDataMetadata(NewDataMetadata storedDataMetadata, LocalDateTime scheduled) {
        this.baseVersionId=storedDataMetadata.baseVersionId;
        this.dataModelVersion=storedDataMetadata.dataModelVersion;
        this.scheduled=scheduled;
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
