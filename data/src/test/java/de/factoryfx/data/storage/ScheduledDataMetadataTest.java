package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ScheduledDataMetadataTest {
    @Test
    public void test_json(){

        ScheduledDataMetadata scheduledDataMetadata = new ScheduledDataMetadata();
        scheduledDataMetadata.scheduled= LocalDateTime.now();
        ScheduledDataMetadata copy= ObjectMapperBuilder.build().copy(scheduledDataMetadata);
        Assert.assertEquals(scheduledDataMetadata.scheduled,copy.scheduled);

    }

}