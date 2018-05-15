package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

/**
 * metadata for a new factory update
 */
public class NewScheduledDataMetadata {

    public NewDataMetadata newDataMetadata;
    public LocalDateTime scheduled;

    @JsonCreator
    public NewScheduledDataMetadata(){

    }

    public NewScheduledDataMetadata(NewDataMetadata newDataMetadata, LocalDateTime scheduled){
        this.newDataMetadata=newDataMetadata;
        this.scheduled = scheduled;
    }

}
