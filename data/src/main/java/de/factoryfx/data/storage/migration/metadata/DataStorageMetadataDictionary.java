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
    private DataStorageMetadataDictionary(@JsonProperty("dataStorageMetadataList") List<DataStorageMetadata> dataStorageMetadataList) {
        this.dataStorageMetadataList = dataStorageMetadataList;
    }

    public DataStorageMetadataDictionary(Set<Class<? extends Data>> dataClasses) {
        dataStorageMetadataList =new ArrayList<>();

        ArrayList<Class<? extends Data>> sortedClasses = new ArrayList<>(dataClasses);
        sortedClasses.sort(Comparator.comparing(Class::getName));
        for (Class<? extends Data> clazz : sortedClasses) {
            if (!Modifier.isAbstract(clazz.getModifiers())){
                dataStorageMetadataList.add(DataDictionary.getDataDictionary(clazz).createDataStorageMetadata());
            }
        }
    }

    public boolean containsClass(String dataClassNameFullQualified) {
        for (DataStorageMetadata dataStorageMetadata : this.dataStorageMetadataList) {
            if (dataStorageMetadata.getClassName().equals(dataClassNameFullQualified)){
                return true;
            }
        }
        return false;
    }

    public boolean containsAttribute(String dataClassNameFullQualified, String previousAttributeName) {
        for (DataStorageMetadata dataStorageMetadata : this.dataStorageMetadataList) {
            if (dataStorageMetadata.getClassName().equals(dataClassNameFullQualified)){
                return dataStorageMetadata.containsAttribute(previousAttributeName);
            }
        }
        return false;
    }
}
