package io.github.factoryfx.factory.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import io.github.factoryfx.factory.FactoryBase;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * the main purpose of SimpleObjectMapper is to get rid of the checked exceptions
 */
public class SimpleObjectMapper {
    private final ObjectMapper objectMapper;

    public SimpleObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T copy(T value) {
        TokenBuffer tokenBuffer = new TokenBuffer(objectMapper, false);
        try {
            objectMapper.writeValue(tokenBuffer, value);
            return readInternal(() -> objectMapper.readValue(tokenBuffer.asParser(), (Class<T>) value.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    public JsonNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public JavaType constructType(Class<?> clazz) {
        return objectMapper.constructType(clazz);
    }

    public TypeFactory getTypeFactory() {
        return objectMapper.getTypeFactory();
    }

    public JsonNode valueToTree(Object object) {
        return objectMapper.valueToTree(object);
    }

    public JsonNode readTree(byte[] content) {
        return readInternal(() -> objectMapper.readTree(content));
    }

    public JsonNode readTree(InputStream in) {
        return readInternal(() -> objectMapper.readTree(in));
    }

    public JsonNode readTree(Path path) {
        return readInternal(() -> objectMapper.readTree(path.toFile()));
    }

    public JsonNode readTree(String content) {
        return readInternal(() -> objectMapper.readTree(content));
    }

    public <T> T readValue(byte[] content, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(content, valueType));
    }

    public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) {
        return readInternal(() -> objectMapper.readValue(src, valueTypeRef));
    }

    public <T> T readValue(File file, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(file, valueType));
    }

    public <T> T readValue(InputStream inputStream, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(inputStream, valueType));
    }

    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) {
        return readInternal(() -> objectMapper.readValue(src, valueTypeRef));
    }

    public <T> T readValue(JsonNode node, Class<T> valueType) {
        return readInternal(() -> objectMapper.treeToValue(node, valueType));
    }

    public <T> T readValue(String content, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(content, valueType));
    }

    public <T> T readValue(String content, JavaType valueType) {
        return readInternal(() -> objectMapper.readValue(content, valueType));
    }

    public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        return readInternal(() -> objectMapper.readValue(content, valueTypeRef));
    }

    public <T> T treeToValue(JsonNode jsonNode, Class<T> rootClass) {
        return readInternal(() -> objectMapper.treeToValue(jsonNode, rootClass));
    }

    public <T> List<T> treeToValueList(JsonNode jsonNode, Class<T> rootClass) {
        return readInternal(() -> objectMapper.readValue(objectMapper.treeAsTokens(jsonNode),
                                                         objectMapper.getTypeFactory().constructCollectionType(List.class, rootClass)));
    }

    public void writeValue(DataOutput out, Object value) {
        writeInternal(() -> objectMapper.writeValue(out, value));
    }

    public void writeValue(File file, Object value) {
        writeInternal(() -> objectMapper.writeValue(file, value));
    }

    public void writeValue(JsonGenerator gen, Object value) {
        writeInternal(() -> objectMapper.writeValue(gen, value));
    }

    public void writeValue(OutputStream out, Object value) {
        writeInternal(() -> objectMapper.writeValue(out, value));
    }

    public void writeValue(Path path, Object value) {
        writeInternal(() -> objectMapper.writeValue(path.toFile(), value));
    }

    public void writeValue(Writer w, Object value) {
        writeInternal(() -> objectMapper.writeValue(w, value));
    }

    public byte[] writeValueAsBytes(Object value) {
        return writeInternal(() -> objectMapper.writeValueAsBytes(value));
    }

    public String writeValueAsString(JsonNode node) {
        return writeInternal(() -> writeValueAsString(objectMapper.treeToValue(node, Object.class)));
    }

    public String writeValueAsString(JsonNode node, OutputStyle outputStyle) {
        return writeInternal(() -> writeValueAsString(objectMapper.treeToValue(node, Object.class), outputStyle));
    }

    public String writeValueAsString(Object value) {
        return writeValueAsString(value, OutputStyle.DEFAULT);
    }

    public String writeValueAsString(Object value, OutputStyle outputStyle) {
        return writeInternal(() -> outputStyle.getWriter(objectMapper).writeValueAsString(value));
    }

    @SuppressWarnings("unchecked")
    private <T> T readInternal(ResultFunction<T> function) {
        try {
            T value = function.apply();
            if (value instanceof FactoryBase<?, ?>) {
                return (T) ((FactoryBase<?, ?>) value).internal().finalise();
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeInternal(VoidFunction function) {
        try {
            function.apply();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T writeInternal(ResultFunction<T> function) {
        try {
            return function.apply();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private interface VoidFunction {
        void apply() throws IOException;
    }

    private interface ResultFunction<T> {
        T apply() throws IOException;
    }
}