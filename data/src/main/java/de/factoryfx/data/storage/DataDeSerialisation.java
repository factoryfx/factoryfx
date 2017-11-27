package de.factoryfx.data.storage;

import de.factoryfx.data.Data;

public interface DataDeSerialisation<R extends Data> {
    boolean canRead(int dataModelVersion);
    R read(String data);
    StoredDataMetadata readStorageMetadata(String data);
    ScheduledDataMetadata readScheduledMetadata(String data);
}
