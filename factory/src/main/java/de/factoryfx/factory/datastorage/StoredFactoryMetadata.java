package de.factoryfx.factory.datastorage;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StoredFactoryMetadata {
    public LocalDateTime creationTime;
    public String id;
    public String user;

    /**the base version on the server*/
    public String baseVersionId;


    @JsonCreator
    public StoredFactoryMetadata() {
        this.creationTime=LocalDateTime.now();
    }
}
