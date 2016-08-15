package de.factoryfx.factory.datastorage;

import java.time.LocalDateTime;

import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class StoredFactoryMetadataTest {

    @Test
    public void test_json(){
        StoredFactoryMetadata value=new StoredFactoryMetadata();
        value.creationTime= LocalDateTime.now();
        value.baseVersionId="sdfgstrg";
        ObjectMapperBuilder.build().copy(value);
    }

}