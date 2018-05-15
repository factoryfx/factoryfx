package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

import java.util.List;

/**
 *
 * @param <R> root
 */
public class DataSerialisationManager<R extends Data,S> {
    private final DataSerialisation<R,S> defaultDataSerialisation;
    private final DataDeSerialisation<R,S> defaultDataDeSerialisation;
    private final List<DataMigration> dataMigrations;
    private final int dataModelVersionForStoring;

    public DataSerialisationManager(DataSerialisation<R,S> defaultDataSerialisation, DataDeSerialisation<R,S> defaultDataDeSerialisation, List<DataMigration> dataMigrations, int dataModelVersion) {
        this.defaultDataDeSerialisation = defaultDataDeSerialisation;
        this.defaultDataSerialisation = defaultDataSerialisation;
        this.dataMigrations = dataMigrations;
        this.dataModelVersionForStoring =dataModelVersion;
    }

    public String write(R root) {
        return defaultDataSerialisation.write(root);
    }

    public String writeStorageMetadata(StoredDataMetadata<S> metadata) {
        return defaultDataSerialisation.writeStorageMetadata(metadata);
    }

    public String writeScheduledMetadata(ScheduledDataMetadata<S> metadata) {
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

    public StoredDataMetadata<S> readStoredFactoryMetadata(String data) {
        return defaultDataDeSerialisation.readStorageMetadata(data);
    }

    public ScheduledDataMetadata<S> readScheduledFactoryMetadata(String data) {
        return defaultDataDeSerialisation.readScheduledMetadata(data);
    }

    public NewDataMetadata prepareNewFactoryMetadata(NewDataMetadata newFactoryMetadata){
        newFactoryMetadata.dataModelVersion= dataModelVersionForStoring;
        return newFactoryMetadata;
    }

}

