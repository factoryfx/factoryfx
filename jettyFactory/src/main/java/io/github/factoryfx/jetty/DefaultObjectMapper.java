package io.github.factoryfx.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

public class DefaultObjectMapper extends ObjectMapper {

    public DefaultObjectMapper(){
        super();
        ObjectMapperBuilder.buildNewObjectMapper(this);
    }

    protected ObjectMapper createImpl() {
        return ObjectMapperBuilder.buildNewObjectMapper();
    }
}
