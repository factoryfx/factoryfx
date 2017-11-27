package de.factoryfx.data.storage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.factoryfx.data.Data;
import org.junit.Test;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends Data {

    }

    @Test
    public void testJsonSerialization() throws IOException {
        DataAndStoredMetadata<EmptyFactory> d = new DataAndStoredMetadata<>(new EmptyFactory(),new StoredDataMetadata());
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        String s = m.writeValueAsString(d);
        m.readValue(s,DataAndStoredMetadata.class);
    }

}