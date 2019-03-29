package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;

public class ClassRename implements DataMigration {
    private final String previousDataClassNameFullQualified;
    private final Class<? extends FactoryBase<?,?>> newDataClass;

    public ClassRename(String previousDataClassNameFullQualified, Class<? extends FactoryBase<?,?>> newDataClass) {
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
