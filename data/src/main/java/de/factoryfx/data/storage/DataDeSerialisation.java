package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

public interface DataDeSerialisation<R extends Data,S> {
    boolean canRead(int dataModelVersion);
    R read(String data);
    StoredDataMetadata<S> readStorageMetadata(String data);
    ScheduledDataMetadata<S> readScheduledMetadata(String data);
}
