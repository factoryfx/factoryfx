package de.factoryfx.factory.datastorage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;

public class JacksonSerialisation<T extends FactoryBase<?,?>> implements FactorySerialisation<T>{

    private final int dataModelVersion;

    public JacksonSerialisation(int dataModelVersion) {
        this.dataModelVersion = dataModelVersion;
    }

    @Override
    public String write(T root) {
        return ObjectMapperBuilder.build().writeValueAsString(root);
    }

    @Override
    public String writeStorageMetadata(StoredFactoryMetadata metadata) {
        metadata.dataModelVersion=dataModelVersion;
        return ObjectMapperBuilder.build().writeValueAsString(metadata);
    }
}
