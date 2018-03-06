package de.factoryfx.server.rest.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.SimpleFactoryBase;

public class DefaultObjectMapperFactory<V> extends SimpleFactoryBase<ObjectMapper,V> {
    @Override
    public ObjectMapper createImpl() {
        return ObjectMapperBuilder.buildNewObjectMapper();
    }
}
