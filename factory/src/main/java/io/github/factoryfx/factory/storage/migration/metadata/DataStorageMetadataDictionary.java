package io.github.factoryfx.factory.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class DataStorageMetadataDictionary {
    @JsonProperty
    final List<DataStorageMetadata> dataList;
    @JsonProperty
    String rootClass;

    @JsonCreator
    public DataStorageMetadataDictionary(@JsonProperty("dataList") List<DataStorageMetadata> dataList, @JsonProperty("rootClass")String rootClass) {
        this.dataList = dataList;
        this.rootClass = rootClass;
    }

    public DataStorageMetadata getDataStorageMetadata(String dataClassNameFullQualified){
        for (DataStorageMetadata dataStorageMetadata : this.dataList) {
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
        for (DataStorageMetadata dataStorageMetadata : this.dataList) {
            if (dataStorageMetadata.getClassName().equals(dataClassNameFullQualified)){
                dataStorageMetadata.renameAttribute(previousAttributeName,newAttributeName);
                return;
            }
        }
        throw new IllegalArgumentException("factory not found: "+dataClassNameFullQualified);
    }

    public void renameClass(String previousDataClassNameFullQualified, String newNameFullQualified) {
        for (DataStorageMetadata dataStorageMetadata : this.dataList) {
            dataStorageMetadata.renameClass(previousDataClassNameFullQualified,newNameFullQualified);
        }
        if (previousDataClassNameFullQualified.equals(rootClass)){
            rootClass=newNameFullQualified;
        }
    }

    public boolean isRemovedAttribute(String dataClass, String previousAttributeName) {
        DataStorageMetadata dataStorageMetadata = getDataStorageMetadata(dataClass);
        if (dataStorageMetadata!=null){
            return dataStorageMetadata.isRemovedAttribute(previousAttributeName);
        }
        return false;
    }

    public boolean isRetypedAttribute(String dataClass, String previousAttributeName) {
        DataStorageMetadata dataStorageMetadata = getDataStorageMetadata(dataClass);
        if (dataStorageMetadata!=null){
            return dataStorageMetadata.isRetypedAttribute(previousAttributeName);
        }
        return false;
    }

    public void markRemovedAttributes(){
        dataList.forEach(DataStorageMetadata::markRemovedAttributes);
    }

    public void markRetypedAttributes(){
        dataList.forEach(DataStorageMetadata::markRetypedAttributes);
    }

    @JsonIgnore
    public DataStorageMetadata getRootDataStorageMetadata(){
        return getDataStorageMetadata(rootClass);
    }

    public void markRemovedClasses() {
        dataList.forEach(DataStorageMetadata::markRemovedClasses);
    }
}
