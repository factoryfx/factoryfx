package de.factoryfx.data.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ObjectMapperBuilder {
    private static SimpleObjectMapper simpleObjectMapper;

    public static SimpleObjectMapper build() {
        if (simpleObjectMapper == null) {
            simpleObjectMapper = buildNew();
        }
        return simpleObjectMapper;
    }

    public static ObjectMapper buildNewObjectMapper() {
        return setupMapper(new JsonFactory());
    }

    public static SimpleObjectMapper buildNew() {
        return new SimpleObjectMapper(setupMapper(new JsonFactory()));
    }

    public static SimpleObjectMapper buildNew(JsonFactory jsonFactory ) {
        return new SimpleObjectMapper(setupMapper(jsonFactory));
    }

    private static ObjectMapper setupMapper(JsonFactory jsonFactory) {
        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        objectMapper.setDefaultMergeable(true); // global default, merging

        return objectMapper;
    }
}
