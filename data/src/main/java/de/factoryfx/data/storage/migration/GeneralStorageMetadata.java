package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Version of the general storage format, how data and attributes are stored
 */
public class GeneralStorageMetadata {
    @JsonProperty
    private final int dataFormatVersion;
    @JsonProperty
    private final int customFormatVersion;//can be used to add migration for project specific attributes

    @JsonCreator
    public GeneralStorageMetadata(@JsonProperty("dataFormatVersion") int dataFormatVersion, @JsonProperty("customFormatVersion") int customFormatVersion) {
        this.dataFormatVersion = dataFormatVersion;
        this.customFormatVersion = customFormatVersion;
    }


    public boolean match(GeneralStorageMetadata generalStorageMetadata) {
        return dataFormatVersion== generalStorageMetadata.dataFormatVersion && customFormatVersion == generalStorageMetadata.customFormatVersion;
    }

    @Override
    public String toString() {
        return "StorageFormat{" + "dataFormatVersion=" + dataFormatVersion + ", customFormatVersion=" + customFormatVersion + '}';
    }
}
