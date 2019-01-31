package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.function.Consumer;

public class DataMigration {

    private final Consumer<DataMigrationApi> migrationExecutor;

    public DataMigration(Consumer<DataMigrationApi> migrationExecutor) {
        this.migrationExecutor = migrationExecutor;
    }

    boolean canMigrate(DataStorageMetadataDictionary pastDataStorageMetadataDictionary){
        DataMigrationApi dataMigrationApi =new DataMigrationApi();
        migrationExecutor.accept(dataMigrationApi);
        return dataMigrationApi.canMigrate(pastDataStorageMetadataDictionary);
    }

    void migrate(JsonNode jsonNode){
        DataMigrationApi dataMigrationApi =new DataMigrationApi();
        migrationExecutor.accept(dataMigrationApi);
        dataMigrationApi.migrate(jsonNode);
    }
}


/*

renanme

 */