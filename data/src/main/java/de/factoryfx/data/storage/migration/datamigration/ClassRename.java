package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;

public class ClassRename implements DataMigrationManager.DataMigration {
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
}
