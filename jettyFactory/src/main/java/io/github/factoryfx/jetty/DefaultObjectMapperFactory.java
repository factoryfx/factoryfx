package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;

public class DefaultObjectMapperFactory<V,R extends FactoryBase<?,R>> extends SimpleFactoryBase<ObjectMapper,R> {
    @Override
    public ObjectMapper createImpl() {
        return ObjectMapperBuilder.buildNewObjectMapper();
    }
}
