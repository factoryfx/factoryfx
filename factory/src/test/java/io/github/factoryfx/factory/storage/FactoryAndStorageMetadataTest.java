package io.github.factoryfx.factory.storage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends FactoryBase<Void, EmptyFactory> {

    }

    @Test
    public void testJsonSerialization() throws IOException {
        DataAndStoredMetadata<EmptyFactory,Void> d = new DataAndStoredMetadata<>(new EmptyFactory(),new StoredDataMetadata<>("","","","",null,null));
        ObjectMapper m = ObjectMapperBuilder.buildNewObjectMapper();
        String s = m.writeValueAsString(d);
        m.readValue(s,DataAndStoredMetadata.class);
    }

}