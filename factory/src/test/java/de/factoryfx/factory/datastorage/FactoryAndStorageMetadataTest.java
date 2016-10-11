package de.factoryfx.factory.datastorage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import org.junit.Test;

public class FactoryAndStorageMetadataTest {

    static final class EmptyFactory extends FactoryBase<Void,Void> {

        @Override
        public LiveCycleController<Void, Void> createLifecycleController() {
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