package de.factoryfx.data.storage;

import java.time.LocalDateTime;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class StoredDataMetadataTest {

    @Test
    public void test_json(){
        StoredDataMetadata value=new StoredDataMetadata();
        final LocalDateTime now = LocalDateTime.now();
        value.creationTime= now;
        value.baseVersionId="sdfgstrg";
        final StoredDataMetadata copy = ObjectMapperBuilder.build().copy(value);
        Assert.assertEquals(now,copy.creationTime);
    }

}