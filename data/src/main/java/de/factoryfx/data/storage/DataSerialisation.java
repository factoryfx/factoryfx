package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

public interface DataSerialisation<R extends Data,S> {

    String write(R root);

    String writeStorageMetadata(StoredDataMetadata<S> metadata);

    default String writeScheduledMetadata(StoredDataMetadata<S> metadata) {
        return writeStorageMetadata(metadata);
    }
}
