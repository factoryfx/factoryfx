package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * metadata for a new factory update
 */
public class NewFactoryMetadata {
    /**
     * the base version on the server
     */
    public String baseVersionId;

    /**
     * version of the factory structure used for migration
     */
    public int dataModelVersion;

    @JsonCreator
    public NewFactoryMetadata(){
    }
}
