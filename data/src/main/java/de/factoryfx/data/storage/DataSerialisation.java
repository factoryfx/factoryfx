package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

public interface DataSerialisation<R extends Data> {

    String write(R root);

    String writeStorageMetadata(StoredDataMetadata metadata);

    default String writeScheduledMetadata(StoredDataMetadata metadata) {
        return writeStorageMetadata(metadata);
    }
}
