package io.github.factoryfx.factory.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.factoryfx.factory.DataObjectIdResolver;

public class ObjectMapperBuilder {
    private static SimpleObjectMapper simpleObjectMapper;

    public static SimpleObjectMapper build() {
        if (simpleObjectMapper == null) {
            simpleObjectMapper = buildNew();
        }
        return simpleObjectMapper;
    }

    public static ObjectMapper buildNewObjectMapper(ObjectMapper objectMapper) {
        return setupMapper(objectMapper);
    }

    public static ObjectMapper buildNewObjectMapper() {
        return setupMapper(new ObjectMapper(new JsonFactory()));
    }


    public static SimpleObjectMapper buildNew() {
        return new SimpleObjectMapper(setupMapper(new ObjectMapper(new JsonFactory())));
    }

    public static SimpleObjectMapper buildNew(ObjectMapper objectMapper) {
        return new SimpleObjectMapper(setupMapper(objectMapper));
    }

    private static ObjectMapper setupMapper(ObjectMapper objectMapper) {

        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        objectMapper.setDefaultMergeable(true); // global default, merging

//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addDeserializer(String.class, new ForceStringDeserializer());
//        objectMapper.registerModule(simpleModule);

//        objectMapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);


        objectMapper.setHandlerInstantiator(new HandlerInstantiator() {
            @Override
            public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
                return null;
            }

            @Override
            public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
                return null;
            }

            @Override
            public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
                return null;
            }

            @Override
            public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated, Class<?> builderClass) {
                return null;
            }

            @Override
            public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
                return null;
            }

            @Override
            public ObjectIdResolver resolverIdGeneratorInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass) {
                return new DataObjectIdResolver();
            }

        });
        return objectMapper;
    }
}
