package de.factoryfx.data.storage;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

public class JacksonDeSerialisation<R extends Data> implements DataDeSerialisation<R> {
    private final Class<R> rootClass;
    private final int dataModelVersion;

    public JacksonDeSerialisation(Class<R> rootClass, int dataModelVersion) {
        this.rootClass = rootClass;
        this.dataModelVersion = dataModelVersion;
    }

    @Override
    public boolean canRead(int dataModelVersion) {
        return this.dataModelVersion==dataModelVersion;
    }

    @Override
    public R read(String data) {
        return ObjectMapperBuilder.build().readValue(data,rootClass).internal().prepareUsableCopy();
    }

    @Override
    public StoredDataMetadata readStorageMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredDataMetadata.class);
    }

    @Override
    public ScheduledDataMetadata readScheduledMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,ScheduledDataMetadata.class);
    }
}
