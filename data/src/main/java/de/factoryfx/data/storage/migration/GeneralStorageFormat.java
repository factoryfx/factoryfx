package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Version of the general storage format, how data and attributes are stored
 */
public class GeneralStorageFormat {
    @JsonProperty
    private final int dataFormatVersion;
    @JsonProperty
    private final int customFormatVersion;//can be used to add migration for project specific attributes

    @JsonCreator
    public GeneralStorageFormat(@JsonProperty("dataFormatVersion") int dataFormatVersion, @JsonProperty("customFormatVersion") int customFormatVersion) {
        this.dataFormatVersion = dataFormatVersion;
        this.customFormatVersion = customFormatVersion;
    }


    public boolean match(GeneralStorageFormat generalStorageFormat) {
        return dataFormatVersion== generalStorageFormat.dataFormatVersion && customFormatVersion == generalStorageFormat.customFormatVersion;
    }

    @Override
    public String toString() {
        return "StorageFormat{" + "dataFormatVersion=" + dataFormatVersion + ", customFormatVersion=" + customFormatVersion + '}';
    }
}
