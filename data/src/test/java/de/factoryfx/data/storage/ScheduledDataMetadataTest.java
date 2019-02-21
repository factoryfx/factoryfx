package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class ScheduledDataMetadataTest {//ScheduledUpdateMetadata
    private ScheduledUpdateMetadata createScheduledDataMetadata(LocalDateTime scheduled){
        return new ScheduledUpdateMetadata("id","user","comment",scheduled,null,null);
    }

    @Test
    public void test_json(){
        ScheduledUpdateMetadata scheduledDataMetadata =createScheduledDataMetadata(LocalDateTime.now());
        ScheduledUpdateMetadata copy= ObjectMapperBuilder.build().copy(scheduledDataMetadata);
        Assertions.assertEquals(scheduledDataMetadata.scheduled,copy.scheduled);
    }

    @Test
    public void test_delay(){
        ScheduledUpdateMetadata scheduledDataMetadata = createScheduledDataMetadata(LocalDateTime.of(2000,1,1,1,1));
        Assertions.assertTrue(scheduledDataMetadata.getDelay(TimeUnit.MILLISECONDS)<0);

        ScheduledUpdateMetadata scheduledDataMetadata2 = createScheduledDataMetadata(LocalDateTime.of(2100,1,1,1,1));
        Assertions.assertTrue(scheduledDataMetadata2.getDelay(TimeUnit.MILLISECONDS)>0);
    }

    @Test
    public void test_compare(){
        ScheduledUpdateMetadata scheduledDataMetadataOld = createScheduledDataMetadata(LocalDateTime.of(2000,1,1,1,1));

        ScheduledUpdateMetadata scheduledDataMetadataNew = createScheduledDataMetadata(LocalDateTime.of(2100,1,1,1,1));

        Assertions.assertTrue(scheduledDataMetadataOld.compareTo(scheduledDataMetadataNew)<0);
    }

    @Test
    public void test_DelayQueue_past(){
        DelayQueue<ScheduledUpdateMetadata> queue = new DelayQueue<>();
        ScheduledUpdateMetadata scheduledDataMetadata = createScheduledDataMetadata(LocalDateTime.of(2000,1,1,1,1));
        queue.offer(scheduledDataMetadata);
        Assertions.assertEquals(1,queue.size());
        Assertions.assertNotNull(queue.poll());
    }

    @Test
    public void test_DelayQueue_past_order(){
        DelayQueue<ScheduledUpdateMetadata> queue = new DelayQueue<>();

        ScheduledUpdateMetadata scheduledDataMetadata1 = createScheduledDataMetadata(LocalDateTime.of(2000,1,1,1,1));
        queue.offer(scheduledDataMetadata1);
        ScheduledUpdateMetadata scheduledDataMetadata2 = createScheduledDataMetadata(LocalDateTime.of(2000,1,1,1,2));
        queue.offer(scheduledDataMetadata2);

        Assertions.assertEquals(2,queue.size());
        Assertions.assertEquals(scheduledDataMetadata1,queue.poll());
        Assertions.assertEquals(scheduledDataMetadata2,queue.poll());
    }


    @Test
    public void test_DelayQueue_future(){
        DelayQueue<ScheduledUpdateMetadata> queue = new DelayQueue<>();
        ScheduledUpdateMetadata scheduledDataMetadata = createScheduledDataMetadata(LocalDateTime.of(2100,1,1,1,1));
        queue.offer(scheduledDataMetadata);
        Assertions.assertEquals(1,queue.size());
        Assertions.assertNull(queue.poll());
    }
}
