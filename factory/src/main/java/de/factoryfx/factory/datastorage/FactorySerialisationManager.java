package de.factoryfx.factory.datastorage;

import java.util.List;

import de.factoryfx.factory.FactoryBase;

public class FactorySerialisationManager<T extends FactoryBase<?,?>> {
    private final int currentDataModelVersion;
    private final FactorySerialisation<T> defaultFactorySerialisation;
    private final FactoryDeSerialisation<T> defaultFactoryDeSerialisation;
    private final List<FactoryDeSerialisation<T>> migratingFactorySerialisation;

    public FactorySerialisationManager(int currentDataModelVersion, FactorySerialisation<T> defaultFactorySerialisation, FactoryDeSerialisation<T> defaultFactoryDeSerialisation, List<FactoryDeSerialisation<T>> migratingFactorySerialisation) {
        this.currentDataModelVersion = currentDataModelVersion;
        this.defaultFactoryDeSerialisation = defaultFactoryDeSerialisation;
        this.defaultFactorySerialisation = defaultFactorySerialisation;
        this.migratingFactorySerialisation = migratingFactorySerialisation;
    }

    public String write(T root) {
        return defaultFactorySerialisation.write(root);
    }

    public String writeStorageMetadata(StoredFactoryMetadata metadata) {
        metadata.dataModelVersion=currentDataModelVersion;
        return defaultFactorySerialisation.writeStorageMetadata(metadata);
    }

    public T read(String data, int dataModelVersion) {
        if (defaultFactoryDeSerialisation.canRead(data,dataModelVersion)){
            return defaultFactoryDeSerialisation.read(data);
        }
        for (FactoryDeSerialisation<T> migration: migratingFactorySerialisation){
            if (migration.canRead(data,dataModelVersion)){
                return defaultFactoryDeSerialisation.read(data);
            }
        }
        throw new IllegalStateException("cant load factory");
    }

    public StoredFactoryMetadata readStoredFactoryMetadata(String data) {
        return defaultFactoryDeSerialisation.readStorageMetadata(data);
    }

}

