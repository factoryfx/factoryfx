package de.factoryfx.data.storage;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

public class JacksonDeSerialisation<R extends Data,S> implements DataDeSerialisation<R,S> {
    private final Class<R> rootClass;
    private final int dataModelVersion;

    /**
     *
     * @param rootClass root class
     * @param dataModelVersion data model version
     */
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
        return ObjectMapperBuilder.build().readValue(data,rootClass).internal().addBackReferences();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StoredDataMetadata<S> readStorageMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredDataMetadata.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ScheduledDataMetadata<S> readScheduledMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,ScheduledDataMetadata.class);
    }
}
