package de.factoryfx.factory.datastorage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;

public class JacksonDeSerialisation<T extends FactoryBase<?,?>> implements FactoryDeSerialisation<T>{
    private final Class<T> rootClass;
    private final int dataModelVersion;

    public JacksonDeSerialisation(Class<T> rootClass, int dataModelVersion) {
        this.rootClass = rootClass;
        this.dataModelVersion = dataModelVersion;
    }

    @Override
    public boolean canRead(int dataModelVersion) {
        return this.dataModelVersion==dataModelVersion;
    }

    @Override
    public T read(String data) {
        return ObjectMapperBuilder.build().readValue(data,rootClass);
    }

    @Override
    public StoredFactoryMetadata readStorageMetadata(String data) {
        return ObjectMapperBuilder.build().readValue(data,StoredFactoryMetadata.class);
    }
}
