package de.factoryfx.data.storage;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * metadata for a new factory update
 */
public class NewDataMetadata {
    /**
     * the base version on the server
     */
    public String baseVersionId;

    @JsonCreator
    public NewDataMetadata(){
    }
}
