package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * metadata for a future data
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScheduledDataMetadata extends StoredDataMetadata {

    /** for scheduled update: date and time of planned activation of this configuration */
    public LocalDateTime scheduled;

    @JsonCreator
    public ScheduledDataMetadata() {
        super();
    }
}
