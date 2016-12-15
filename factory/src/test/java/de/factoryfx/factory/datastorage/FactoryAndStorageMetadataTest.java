package de.factoryfx.factory.datastorage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.factoryfx.factory.SimpleFactoryBase;
import org.junit.Test;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends SimpleFactoryBase<Void,Void> {

        @Override
        public Void createImpl() {
            return null;
        }
    };

    @Test
    public void testJsonSerialization() throws IOException {
        FactoryAndStorageMetadata<EmptyFactory> d = new FactoryAndStorageMetadata<>(new EmptyFactory(),new StoredFactoryMetadata());
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        String s = m.writeValueAsString(d);
        m.readValue(s,FactoryAndStorageMetadata.class);
    }

}