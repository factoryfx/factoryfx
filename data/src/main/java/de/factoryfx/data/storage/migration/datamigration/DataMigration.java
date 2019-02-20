package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import java.util.List;

public interface DataMigration {
    boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary);
    void migrate(List<DataJsonNode> dataJsonNodes);
}
