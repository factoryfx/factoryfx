package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.NewDataMetadata;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @param <R> root
 * @param <S> summary
 */
public class MigrationManager<R extends Data,S> {
    private final Class<R> rootClass;
    private final List<DataMigration> dataMigrations;
    private final GeneralStorageFormat generalStorageFormat;
    private final List<GeneralMigration> storageFormatMigrations;
    private final DataStorageMetadataDictionary dataStorageMetadataDictionary;

    public MigrationManager(Class<R> rootClass, List<GeneralMigration> generalStorageFormatMigrations, GeneralStorageFormat generalStorageFormat, List<DataMigration> dataMigrations, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        this.rootClass = rootClass;
        this.dataMigrations = dataMigrations;
        this.generalStorageFormat = generalStorageFormat;
        this.storageFormatMigrations = generalStorageFormatMigrations;
        this.dataStorageMetadataDictionary = dataStorageMetadataDictionary;
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

        return read(ObjectMapperBuilder.build().writeTree(jsonNode));
    }

    @SuppressWarnings("unchecked")
    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredDataMetadata.class);
    }

    @SuppressWarnings("unchecked")
    public ScheduledDataMetadata<S> readScheduledFactoryMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,ScheduledDataMetadata.class);
    }

    public NewDataMetadata prepareNewFactoryMetadata(NewDataMetadata newFactoryMetadata){
        return newFactoryMetadata;
    }

    private R read(String data) {
        return ObjectMapperBuilder.build().readValue(data,rootClass).internal().addBackReferences();
    }

    public StoredDataMetadata<S> createStoredDataMetadata(String user, String comment, String baseVersionId, S changeSummary) {
        return new StoredDataMetadata<S>(LocalDateTime.now(),UUID.randomUUID().toString(),user,comment,baseVersionId,changeSummary, generalStorageFormat,dataStorageMetadataDictionary);
    }

    public ScheduledDataMetadata<S> createScheduledDataMetadata(String user, String comment, String baseVersionId, S changeSummary, LocalDateTime scheduled) {
        return new ScheduledDataMetadata<>(LocalDateTime.now(),UUID.randomUUID().toString(),user,comment,baseVersionId,changeSummary, generalStorageFormat,dataStorageMetadataDictionary,scheduled);
    }



}

