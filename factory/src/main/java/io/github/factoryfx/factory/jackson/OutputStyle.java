package io.github.factoryfx.factory.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.function.Function;

public enum OutputStyle {
    PRETTY(ObjectMapper::writerWithDefaultPrettyPrinter),
    COMPACT(objectMapper -> objectMapper.writer().without(SerializationFeature.INDENT_OUTPUT)),
    DEFAULT(ObjectMapper::writer);

    private final Function<ObjectMapper, ObjectWriter> mapperFunction;

    OutputStyle(Function<ObjectMapper, ObjectWriter> mapperFunction) {
        this.mapperFunction = mapperFunction;
    }

    public ObjectWriter getWriter(ObjectMapper objectMapper) {
        return mapperFunction.apply(objectMapper);
    }
}