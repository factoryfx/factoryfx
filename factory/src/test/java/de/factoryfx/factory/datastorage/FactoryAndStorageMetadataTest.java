package de.factoryfx.factory.datastorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LifecycleNotifier;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends FactoryBase<Void,Void> {

        @Override
        protected Void createImp(Optional<Void> previousLiveObject, LifecycleNotifier<Void> lifecycle) {
            return null;
        }
    };

    @Test
    public void testJsonSerialization() throws IOException {
        FactoryAndStorageMetadata d = new FactoryAndStorageMetadata(new EmptyFactory(),new StoredFactoryMetadata());
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        String s = m.writeValueAsString(d);
        m.readValue(s,FactoryAndStorageMetadata.class);
    }

}