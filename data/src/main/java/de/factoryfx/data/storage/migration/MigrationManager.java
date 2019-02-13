package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;

import java.util.List;

/**
 * @param <R> root
 * @param <S> summary
 */
public class MigrationManager<R extends Data,S> {
    private final Class<R> rootClass;
    private final DataMigrationManager dataMigration;
    private final GeneralStorageMetadata generalStorageMetadata;
    private final List<GeneralMigration> storageFormatMigrations;
    private final SimpleObjectMapper objectMapper;

    public MigrationManager(Class<R> rootClass, List<GeneralMigration> generalStorageFormatMigrations, GeneralStorageMetadata generalStorageMetadata, DataMigrationManager dataMigration, SimpleObjectMapper objectMapper) {
        this.rootClass = rootClass;
        this.dataMigration = dataMigration;
        this.generalStorageMetadata = generalStorageMetadata;
        this.storageFormatMigrations = generalStorageFormatMigrations;
        this.objectMapper = objectMapper;
    }

    public String write(R root) {
        return objectMapper.writeValueAsString(root);
    }

    public String writeStorageMetadata(StoredDataMetadata<S> metadata) {
        return objectMapper.writeValueAsString(metadata);
    }

    public String writeScheduledMetadata(StoredDataMetadata<S> metadata) {
        return  objectMapper.writeValueAsString(metadata);
    }

//    private boolean canRead(int dataModelVersion) {
//        return dataModelVersionForStoring == dataModelVersion;
//    }

    public R read(JsonNode data, StoredDataMetadata<S> metadata) {
        return read(objectMapper.writeTree(data),metadata);
    }

    public R read(String data, StoredDataMetadata<S> metadata) {
        GeneralStorageMetadata currentFormat= metadata.generalStorageMetadata;
        String migratedData = data;
        if (currentFormat==null){//old data from old migration system
            currentFormat=new GeneralStorageMetadata(1,0);
        }
        while (!generalStorageMetadata.match(currentFormat)){
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

        JsonNode jsonNode = objectMapper.readTree(migratedData);
        dataMigration.migrate(jsonNode,metadata.dataStorageMetadataDictionary);
        return objectMapper.treeToValue(jsonNode,rootClass).internal().addBackReferences();
    }

    @SuppressWarnings("unchecked")
    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return objectMapper.readValue(data,StoredDataMetadata.class);
    }

    @SuppressWarnings("unchecked")
    public ScheduledDataMetadata<S> readScheduledFactoryMetadata(String data) {
        return objectMapper.readValue(data,ScheduledDataMetadata.class);
    }


}

