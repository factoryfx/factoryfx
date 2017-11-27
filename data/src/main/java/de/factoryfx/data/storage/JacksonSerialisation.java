package de.factoryfx.data.storage;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;

public class JacksonSerialisation<R extends Data> implements DataSerialisation<R> {

    private final int dataModelVersion;

    public JacksonSerialisation(int dataModelVersion) {
        this.dataModelVersion = dataModelVersion;
    }

    @Override
    public String write(R root) {
        return ObjectMapperBuilder.build().writeValueAsString(root);
    }

    @Override
    public String writeStorageMetadata(StoredDataMetadata metadata) {
        metadata.dataModelVersion=dataModelVersion;
        return ObjectMapperBuilder.build().writeValueAsString(metadata);
    }
}
