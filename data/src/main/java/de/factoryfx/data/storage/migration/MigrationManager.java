package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;

import java.util.List;

/**
 * @param <R> root
 * @param <S> summary
 */
public class MigrationManager<R extends Data,S> {
    private final Class<R> rootClass;
    private final List<DataMigration> dataMigrations;
    private final GeneralStorageFormat generalStorageFormat;
    private final List<GeneralMigration> storageFormatMigrations;

    public MigrationManager(Class<R> rootClass, List<GeneralMigration> generalStorageFormatMigrations, GeneralStorageFormat generalStorageFormat, List<DataMigration> dataMigrations) {
        this.rootClass = rootClass;
        this.dataMigrations = dataMigrations;
        this.generalStorageFormat = generalStorageFormat;
        this.storageFormatMigrations = generalStorageFormatMigrations;
    }

    public String write(R root) {
        return ObjectMapperBuilder.build().writeValueAsString(root);
    }

    public String writeStorageMetadata(StoredDataMetadata<S> metadata) {
        return ObjectMapperBuilder.build().writeValueAsString(metadata);
    }

    public String writeScheduledMetadata(StoredDataMetadata<S> metadata) {
        return  ObjectMapperBuilder.build().writeValueAsString(metadata);
    }

//    private boolean canRead(int dataModelVersion) {
//        return dataModelVersionForStoring == dataModelVersion;
//    }

    public R read(JsonNode data, StoredDataMetadata<S> metadata) {
        return read(ObjectMapperBuilder.build().writeTree(data),metadata);
    }

    public R read(String data, StoredDataMetadata<S> metadata) {
        GeneralStorageFormat currentFormat= metadata.generalStorageFormat;
        String migratedData = data;
        while (!generalStorageFormat.match(currentFormat)){
            boolean foundMigration=false;
            for (GeneralMigration migration: storageFormatMigrations){
                if (migration.canMigrate(currentFormat)){
                    migratedData = migration.migrate(migratedData);
                    currentFormat = migration.migrationResultStorageFormat();
                    foundMigration=true;
                    break;
                }
            }
            if (!foundMigration){
                throw new IllegalStateException("cant find migration for: "+ currentFormat);
            }
        }

        JsonNode jsonNode = ObjectMapperBuilder.build().readTree(migratedData);
        for (DataMigration dataMigration : dataMigrations) {
            if (dataMigration.canMigrate(metadata.dataStorageMetadataDictionary)){
                dataMigration.migrate(jsonNode);
            }
        }

        return ObjectMapperBuilder.build().treeToValue(jsonNode,rootClass).internal().addBackReferences();
    }

    @SuppressWarnings("unchecked")
    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredDataMetadata.class);
    }

    @SuppressWarnings("unchecked")
    public ScheduledDataMetadata<S> readScheduledFactoryMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,ScheduledDataMetadata.class);
    }


}

