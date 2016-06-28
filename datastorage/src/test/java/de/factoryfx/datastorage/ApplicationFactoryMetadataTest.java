package de.factoryfx.datastorage;

import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class ApplicationFactoryMetadataTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test_json(){
        ApplicationFactoryMetadata applicationFactoryMetadata = new ApplicationFactoryMetadata(null);
        ObjectMapperBuilder.build().copy(applicationFactoryMetadata);
    }

}