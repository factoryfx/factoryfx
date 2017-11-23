package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * metadata for a stored historical factory
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScheduledFactoryMetadata extends StoredFactoryMetadata {

    /** for scheduled update: date and time of planned activation of this configuration */
    public LocalDateTime scheduled;

    @JsonCreator
    public ScheduledFactoryMetadata() {
        super();
    }
}
