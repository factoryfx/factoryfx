package de.factoryfx.factory.datastorage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;

public class JacksonSerialisation<T extends FactoryBase<?,?>> implements FactorySerialisation<T>{

    @Override
    public String write(T root) {
        return ObjectMapperBuilder.build().writeValueAsString(root);
    }

    @Override
    public String writeStorageMetadata(StoredFactoryMetadata metadata) {
        return ObjectMapperBuilder.build().writeValueAsString(metadata);
    }
}
