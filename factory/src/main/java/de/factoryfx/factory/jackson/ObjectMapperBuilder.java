package de.factoryfx.factory.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public class ObjectMapperBuilder {
    private static SimpleObjectMapper simpleObjectMapper;

    public static SimpleObjectMapper build() {
        if (simpleObjectMapper == null) {


            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

            SimpleModule m = new SimpleModule();
            m.addAbstractTypeMapping(ObservableMap.class, ObservableMapJacksonAbleWrapper.class);
            m.addAbstractTypeMapping(ObservableList.class, ObservableListJacksonAbleWrapper.class);
            m.addAbstractTypeMapping(ObservableSet.class, ObservableSetJacksonAbleWrapper.class);
            objectMapper.registerModule(m);

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.registerModule(new Jdk7Module());
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//            objectMapper.enableDefaultTyping();

            simpleObjectMapper = new SimpleObjectMapper(objectMapper);
        }
        return simpleObjectMapper;
    }
}
