package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class ApplicationFactoryMetadataTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test_json(){
        StoredDataMetadata applicationFactoryMetadata = new StoredDataMetadata();
        ObjectMapperBuilder.build().copy(applicationFactoryMetadata); //test json serializable
    }

}