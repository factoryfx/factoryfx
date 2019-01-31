package de.factoryfx.data.storage.migration.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.storage.migration.DataJsonNode;

import java.util.*;

public class DataStorageMetadataDictionary {
    @JsonProperty
    final List<DataStorageMetadata> dataStorageMetadataList;

    @JsonCreator
    private DataStorageMetadataDictionary(@JsonProperty("dataStorageMetadataList") List<DataStorageMetadata> dataStorageMetadataList) {
        this.dataStorageMetadataList = dataStorageMetadataList;
    }

    public DataStorageMetadataDictionary(Class<? extends Data> rootClazz) {
        Set<Class<? extends Data>> dataClasses = new HashSet<>();
        DataDictionary.getDataDictionary(rootClazz).collectDataClassesDeep(dataClasses);

        dataStorageMetadataList =new ArrayList<>();

        ArrayList<Class<? extends Data>> sortedClasses = new ArrayList<>(dataClasses);
        sortedClasses.sort(Comparator.comparing(Class::getName));
        for (Class<? extends Data> clazz : sortedClasses) {
            dataStorageMetadataList.add(DataDictionary.getDataDictionary(clazz).createDataStorageMetadata());
        }
    }

    public CompatibleCheckResult compatibleCheck(DataStorageMetadataDictionary newDataStorageMetadataDictionary){
        CompatibleCheckResult compatibleCheckResult = new CompatibleCheckResult();
        for (DataStorageMetadata dataStorageMetadata : dataStorageMetadataList) {
            for (DataStorageMetadata newDataStorageMetadata : newDataStorageMetadataDictionary.dataStorageMetadataList) {
                dataStorageMetadata.compatibleCheck(newDataStorageMetadata,compatibleCheckResult);
            }
        }
        return compatibleCheckResult;
    }

    public int createDataModelVersion() {
        return Objects.hash(dataStorageMetadataList);
    }

    public void removeDeletedAttributes(DataStorageMetadataDictionary previous, List<DataJsonNode> previousDataList) {
        Map<String,DataStorageMetadata> classNameToDataCurrent= new HashMap<>();
        Map<String,DataStorageMetadata> classNameToDataPrevious= new HashMap<>();
        for (DataStorageMetadata dataStorageMetadata : dataStorageMetadataList) {
            classNameToDataCurrent.put(dataStorageMetadata.getClassName(),dataStorageMetadata);
        }
        for (DataStorageMetadata dataStorageMetadata : previous.dataStorageMetadataList) {
            classNameToDataPrevious.put(dataStorageMetadata.getClassName(),dataStorageMetadata);
        }


        for (DataJsonNode dataJsonNode: previousDataList) {
            DataStorageMetadata currentMetadata = classNameToDataCurrent.get(dataJsonNode.getDataClassName());
            DataStorageMetadata previousMetadata = classNameToDataPrevious.get(dataJsonNode.getDataClassName());

            List<String> removedAttributes=  currentMetadata.getRemovedAttributes(previousMetadata);
            for (String removedAttribute : removedAttributes) {
                dataJsonNode.removeAttribute(removedAttribute);
            }
        }

    }

    public boolean match(DataStorageMetadataDictionary dataDictionary) {
        if (dataStorageMetadataList.size()!=dataDictionary.dataStorageMetadataList.size()){
            return false;
        }
        for (int i = 0; i < dataStorageMetadataList.size(); i++) {
            DataStorageMetadata dataStorageMetadata = dataStorageMetadataList.get(i);
            if (!dataStorageMetadata.match(dataDictionary.dataStorageMetadataList.get(i))) {
                return false;
            }
        }
        return true;
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
