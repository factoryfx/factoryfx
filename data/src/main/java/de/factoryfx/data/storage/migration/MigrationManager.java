package de.factoryfx.data.storage.migration;

import com.fasterxml.jackson.databind.JsonNode;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.SimpleObjectMapper;
import de.factoryfx.data.storage.ScheduledUpdateMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

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

    public R read(JsonNode data, GeneralStorageMetadata generalStorageMetadata, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return read(objectMapper.writeTree(data),generalStorageMetadata,dataStorageMetadataDictionary);
    }

    public R read(String data, GeneralStorageMetadata generalStorageMetadata, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        GeneralStorageMetadata currentFormat= generalStorageMetadata;
        String migratedData = data;
        if (currentFormat==null){//old data from old migration system
            currentFormat=new GeneralStorageMetadata(1,0);
        }
        while (!this.generalStorageMetadata.match(currentFormat)){
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
        dataMigration.migrate(jsonNode,dataStorageMetadataDictionary);
        return objectMapper.treeToValue(jsonNode,rootClass).internal().addBackReferences();
    }

    @SuppressWarnings("unchecked")
    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return objectMapper.readValue(data,StoredDataMetadata.class);
    }

    public ScheduledUpdateMetadata readScheduledFactoryMetadata(String data) {
        return objectMapper.readValue(data,ScheduledUpdateMetadata.class);
    }

    public String writeScheduledUpdateMetadata(ScheduledUpdateMetadata metadata) {
        return  objectMapper.writeValueAsString(metadata);
    }


}

