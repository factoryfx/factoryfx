package de.factoryfx.factory.datastorage;

import java.time.LocalDateTime;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class StoredFactoryMetadataTest {

    @Test
    public void test_json(){
        StoredFactoryMetadata value=new StoredFactoryMetadata();
        final LocalDateTime now = LocalDateTime.now();
        value.creationTime= now;
        value.baseVersionId="sdfgstrg";
        final StoredFactoryMetadata copy = ObjectMapperBuilder.build().copy(value);
        Assert.assertEquals(now,copy.creationTime);
    }

}