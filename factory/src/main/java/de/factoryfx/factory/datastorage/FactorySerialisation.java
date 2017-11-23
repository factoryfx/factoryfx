package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.FactoryBase;

public interface FactorySerialisation<T extends FactoryBase<?,?>> {

    String write(T root);

    String writeStorageMetadata(StoredFactoryMetadata metadata);

    default String writeScheduledMetadata(StoredFactoryMetadata metadata) {
        return writeStorageMetadata(metadata);
    }
}
