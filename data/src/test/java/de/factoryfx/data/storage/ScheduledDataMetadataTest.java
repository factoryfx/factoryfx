package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ScheduledDataMetadataTest {
    @Test
    public void test_json(){

        ScheduledDataMetadata scheduledDataMetadata = new ScheduledDataMetadata();
        scheduledDataMetadata.scheduled= LocalDateTime.now();
        ScheduledDataMetadata copy= ObjectMapperBuilder.build().copy(scheduledDataMetadata);
        Assert.assertEquals(scheduledDataMetadata.scheduled,copy.scheduled);

    }

    @Test
    public void test_delay(){

        ScheduledDataMetadata scheduledDataMetadata = new ScheduledDataMetadata();
        scheduledDataMetadata.scheduled= LocalDateTime.of(2000,1,1,1,1);
        Assert.assertTrue(scheduledDataMetadata.getDelay(TimeUnit.MILLISECONDS)<0);


        scheduledDataMetadata.scheduled= LocalDateTime.of(2100,1,1,1,1);
        Assert.assertTrue(scheduledDataMetadata.getDelay(TimeUnit.MILLISECONDS)>0);
    }

    @Test
    public void test_compare(){

        ScheduledDataMetadata scheduledDataMetadataOld = new ScheduledDataMetadata();
        scheduledDataMetadataOld.scheduled= LocalDateTime.of(2000,1,1,1,1);

        ScheduledDataMetadata scheduledDataMetadataNew = new ScheduledDataMetadata();
        scheduledDataMetadataNew.scheduled= LocalDateTime.of(2100,1,1,1,1);

        Assert.assertTrue(scheduledDataMetadataOld.compareTo(scheduledDataMetadataNew)<0);
    }

    @Test
    public void test_DelayQueue_past(){
        DelayQueue<ScheduledDataMetadata> queue = new DelayQueue<>();
        ScheduledDataMetadata scheduledDataMetadata = new ScheduledDataMetadata();
        scheduledDataMetadata.scheduled= LocalDateTime.of(2000,1,1,1,1);
        queue.offer(scheduledDataMetadata);
        Assert.assertEquals(1,queue.size());
        Assert.assertNotNull(queue.poll());
    }

    @Test
    public void test_DelayQueue_past_order(){
        DelayQueue<ScheduledDataMetadata> queue = new DelayQueue<>();

        ScheduledDataMetadata scheduledDataMetadata1 = new ScheduledDataMetadata();
        scheduledDataMetadata1.scheduled= LocalDateTime.of(2000,1,1,1,1);
        queue.offer(scheduledDataMetadata1);
        ScheduledDataMetadata scheduledDataMetadata2 = new ScheduledDataMetadata();
        scheduledDataMetadata2.scheduled= LocalDateTime.of(2000,1,1,1,2);
        queue.offer(scheduledDataMetadata2);

        Assert.assertEquals(2,queue.size());
        Assert.assertEquals(scheduledDataMetadata1,queue.poll());
        Assert.assertEquals(scheduledDataMetadata2,queue.poll());
    }


    @Test
    public void test_DelayQueue_future(){
        DelayQueue<ScheduledDataMetadata> queue = new DelayQueue<>();
        ScheduledDataMetadata scheduledDataMetadata = new ScheduledDataMetadata();
        scheduledDataMetadata.scheduled= LocalDateTime.of(2100,1,1,1,1);
        queue.offer(scheduledDataMetadata);
        Assert.assertEquals(1,queue.size());
        Assert.assertNull(queue.poll());
    }
}
