package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

import java.util.List;

/**
 *
 * @param <R> root
 */
public class DataSerialisationManager<R extends Data> {
    private final DataSerialisation<R> defaultDataSerialisation;
    private final DataDeSerialisation<R> defaultDataDeSerialisation;
    private final List<DataMigration> dataMigrations;
    private final int dataModelVersion;

    public DataSerialisationManager(DataSerialisation<R> defaultDataSerialisation, DataDeSerialisation<R> defaultDataDeSerialisation, List<DataMigration> dataMigrations, int dataModelVersion) {
        this.defaultDataDeSerialisation = defaultDataDeSerialisation;
        this.defaultDataSerialisation = defaultDataSerialisation;
        this.dataMigrations = dataMigrations;
        this.dataModelVersion=dataModelVersion;
    }

    public String write(R root) {
        return defaultDataSerialisation.write(root);
    }

    public String writeStorageMetadata(StoredDataMetadata metadata) {
        return defaultDataSerialisation.writeStorageMetadata(metadata);
    }

    public String writeScheduledMetadata(ScheduledDataMetadata metadata) {
        return defaultDataSerialisation.writeScheduledMetadata(metadata);
    }

    public R read(String data, int dataModelVersion) {
        if (defaultDataDeSerialisation.canRead(dataModelVersion)){
            return defaultDataDeSerialisation.read(data);
        }
        String migratedData=data;
        int migrateDataModelVersion=dataModelVersion;
        while (!defaultDataDeSerialisation.canRead(migrateDataModelVersion)){
            boolean foundMigration=false;
            for (DataMigration migration: dataMigrations){
                if (migration.canMigrate(migrateDataModelVersion)){
                    migratedData = migration.migrate(migratedData);
                    migrateDataModelVersion = migration.migrateResultVersion();
                    foundMigration=true;
                    break;
                }
            }
            if (!foundMigration){
                throw new IllegalStateException("cant find migration for: "+migrateDataModelVersion);
            }
        }
        return defaultDataDeSerialisation.read(migratedData);
    }

    public StoredDataMetadata readStoredFactoryMetadata(String data) {
        return defaultDataDeSerialisation.readStorageMetadata(data);
    }

    public ScheduledDataMetadata readScheduledFactoryMetadata(String data) {
        return defaultDataDeSerialisation.readScheduledMetadata(data);
    }

    public NewDataMetadata prepareNewFactoryMetadata(NewDataMetadata newFactoryMetadata){
        newFactoryMetadata.dataModelVersion=dataModelVersion;
        return newFactoryMetadata;
    }

}

