package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class GeneralStorageMetadataTest {
    @Test
    public void test_json(){
        GeneralStorageMetadata value=new GeneralStorageMetadata(1,2);
        GeneralStorageMetadata copy = ObjectMapperBuilder.build().copy(value);

        assertTrue(copy.match(new GeneralStorageMetadata(1, 2)));

    }
}