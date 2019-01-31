package de.factoryfx.data.jackson;

import java.io.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.data.Data;

public class SimpleObjectMapper {
    private final ObjectMapper objectMapper;

    public SimpleObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T copy(T value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeValue(value, out);
        return readValue(new ByteArrayInputStream(out.toByteArray()), (Class<T>) value.getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> T readInternal(ReaderFunction<T> function) {
        try {
            T value = function.read();
            if (value instanceof Data) {
                return (T) ((Data) value).internal().addBackReferences();
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readValue(String content, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(content, valueType));
    }

    public <T> T readValue(File file, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(file, valueType));
    }

    public <T> T readValue(InputStream inputStream, Class<T> valueType) {
        return readInternal(() -> objectMapper.readValue(inputStream, valueType));
    }

    public void writeValue(Object object, OutputStream out) {
        try {
            objectMapper.writeValue(out, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeValue(File file, Object s) {
        try {
            objectMapper.writeValue(file, s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode readTree(String content){
        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeTree(JsonNode node){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Object value = objectMapper.treeToValue(node, Object.class);
            return writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private interface ReaderFunction<T> {
        T read() throws IOException;
    }

}
