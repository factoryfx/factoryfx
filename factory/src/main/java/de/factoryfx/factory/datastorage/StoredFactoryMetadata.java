package de.factoryfx.factory.datastorage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * metadata for a stored historical factory
 */
public class StoredFactoryMetadata {
    public LocalDateTime creationTime;
    public String id;
    public String user;
    public String comment;

    /**the base version on the server*/
    public String baseVersionId;

    /** version of the factory structure used for migration*/
    public int dataModelVersion;

    @JsonCreator
    public StoredFactoryMetadata() {
        this.creationTime=LocalDateTime.now();
    }
}
