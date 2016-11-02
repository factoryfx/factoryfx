package de.factoryfx.factory.datastorage;

import java.util.List;

import de.factoryfx.factory.FactoryBase;

public class FactorySerialisationManager<T extends FactoryBase<?,?>> {
    private final FactorySerialisation<T> defaultFactorySerialisation;
    private final FactoryDeSerialisation<T> defaultFactoryDeSerialisation;
    private final List<FactoryMigration> factoryMigrations;

    public FactorySerialisationManager(FactorySerialisation<T> defaultFactorySerialisation, FactoryDeSerialisation<T> defaultFactoryDeSerialisation, List<FactoryMigration> factoryMigrations) {
        this.defaultFactoryDeSerialisation = defaultFactoryDeSerialisation;
        this.defaultFactorySerialisation = defaultFactorySerialisation;
        this.factoryMigrations = factoryMigrations;
    }

    public String write(T root) {
        return defaultFactorySerialisation.write(root);
    }

    public String writeStorageMetadata(StoredFactoryMetadata metadata) {
        return defaultFactorySerialisation.writeStorageMetadata(metadata);
    }

    public T read(String data, int dataModelVersion) {
        if (defaultFactoryDeSerialisation.canRead(dataModelVersion)){
            return defaultFactoryDeSerialisation.read(data);
        }
        String migratedData=data;
        int migrateDataModelVersion=dataModelVersion;
        while (!defaultFactoryDeSerialisation.canRead(migrateDataModelVersion)){
            boolean foundMigration=false;
            for (FactoryMigration migration: factoryMigrations){
                if (migration.canMigrate(migrateDataModelVersion)){
                    migratedData = migration.migrate(data);
                    migrateDataModelVersion = migration.migrateResultVersion();
                    foundMigration=true;
                    break;
                }
            }
            if (!foundMigration){
                throw new IllegalStateException("cant find migration for: "+migrateDataModelVersion);
            }
        }
        return defaultFactoryDeSerialisation.read(migratedData);
    }

    public StoredFactoryMetadata readStoredFactoryMetadata(String data) {
        return defaultFactoryDeSerialisation.readStorageMetadata(data);
    }

}

