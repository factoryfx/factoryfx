package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;

import java.lang.reflect.Modifier;
import java.util.*;

public class DataStorageMetadataDictionary {
    @JsonProperty
    final List<DataStorageMetadata> dataStorageMetadataList;

    @JsonCreator
    public DataStorageMetadataDictionary(@JsonProperty("dataStorageMetadataList") List<DataStorageMetadata> dataStorageMetadataList) {
        this.dataStorageMetadataList = dataStorageMetadataList;
    }

    private DataStorageMetadata getDataStorageMetadata(String dataClassNameFullQualified){
        for (DataStorageMetadata dataStorageMetadata : this.dataStorageMetadataList) {
            if (dataStorageMetadata.getClassName().equals(dataClassNameFullQualified)){
                return dataStorageMetadata;
            }
        }
        return null;
    }

    public boolean containsClass(String dataClassNameFullQualified) {
        return getDataStorageMetadata(dataClassNameFullQualified)!=null;
    }

    public boolean containsAttribute(String dataClassNameFullQualified, String previousAttributeName) {
        DataStorageMetadata dataStorageMetadata = getDataStorageMetadata(dataClassNameFullQualified);
        if (dataStorageMetadata!=null){
            return dataStorageMetadata.containsAttribute(previousAttributeName);
        }
        return false;
    }

    public boolean isSingleton(String fullQualifiedName) {
        DataStorageMetadata dataStorageMetadata = getDataStorageMetadata(fullQualifiedName);
        if (dataStorageMetadata!=null){
            return dataStorageMetadata.isSingleton();
        }
        return false;
    }

    public void renameAttribute(String dataClassNameFullQualified, String previousAttributeName, String newAttributeName) {
        for (DataStorageMetadata dataStorageMetadata : this.dataStorageMetadataList) {
            if (dataStorageMetadata.getClassName().equals(dataClassNameFullQualified)){
                dataStorageMetadata.renameAttribute(previousAttributeName,newAttributeName);
            }
        }
    }

    public void renameClass(String previousDataClassNameFullQualified, String newNameFullQualified) {
        for (DataStorageMetadata dataStorageMetadata : this.dataStorageMetadataList) {
            if (dataStorageMetadata.getClassName().equals(previousDataClassNameFullQualified)){
                dataStorageMetadata.renameClass(newNameFullQualified);
            }
        }
    }

    public boolean isRemovedAttribute(String dataClass, String previousAttributeName) {
        return !containsClass(dataClass) || !containsAttribute(dataClass,previousAttributeName);
    }
}
