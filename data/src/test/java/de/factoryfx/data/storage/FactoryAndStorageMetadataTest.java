package de.factoryfx.data.storage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends Data {

    }

    @Test
    public void testJsonSerialization() throws IOException {
        DataAndStoredMetadata<EmptyFactory,Void> d = new DataAndStoredMetadata<>(new EmptyFactory(),new StoredDataMetadata<>("","","","",0,null));
        ObjectMapper m = ObjectMapperBuilder.buildNewObjectMapper();
        String s = m.writeValueAsString(d);
        m.readValue(s,DataAndStoredMetadata.class);
    }

}