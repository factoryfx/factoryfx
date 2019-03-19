package io.github.factoryfx.data.storage.migration.datamigration;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;

public class ClassRename implements DataMigration {
    private final String previousDataClassNameFullQualified;
    private final Class<? extends Data> newDataClass;

    public ClassRename(String previousDataClassNameFullQualified, Class<? extends Data> newDataClass) {
        this.previousDataClassNameFullQualified = previousDataClassNameFullQualified;
        this.newDataClass = newDataClass;
    }

    @Override
    public boolean canMigrate(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return dataStorageMetadataDictionary.containsClass(previousDataClassNameFullQualified);
    }

    public void migrate(List<DataJsonNode> dataJsonNodes) {
        dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(previousDataClassNameFullQualified)).forEach(dataJsonNode -> {
            dataJsonNode.renameClass(newDataClass);
        });
    }

    public void updateDataStorageMetadataDictionary(DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        dataStorageMetadataDictionary.renameClass(previousDataClassNameFullQualified,newDataClass.getName());
    }
}