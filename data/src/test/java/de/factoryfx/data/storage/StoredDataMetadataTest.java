package de.factoryfx.data.storage;

import java.time.LocalDateTime;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class StoredDataMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }


    @Test
    public void test_json(){
        LocalDateTime now = LocalDateTime.now();
        StoredDataMetadata<SummaryDummy> value=new StoredDataMetadata<>(now, "", "", "", "sdfgstrg",0, new SummaryDummy());
        final StoredDataMetadata<SummaryDummy> copy = ObjectMapperBuilder.build().copy(value);
        Assert.assertEquals(now,copy.creationTime);
        Assert.assertEquals(1,copy.changeSummary.diffCounter);
    }

}