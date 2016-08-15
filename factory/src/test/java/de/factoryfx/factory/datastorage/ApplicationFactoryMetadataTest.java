package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class ApplicationFactoryMetadataTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test_json(){
        StoredFactoryMetadata applicationFactoryMetadata = new StoredFactoryMetadata();
        ObjectMapperBuilder.build().copy(applicationFactoryMetadata); //test json serializable
    }

}