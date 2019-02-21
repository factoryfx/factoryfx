package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DataAndScheduledMetadataTest {

    @Test
    public void test_json(){
        ScheduledUpdate<ExampleDataA> test = new ScheduledUpdate<>(new ExampleDataA(),"fg","dsad","dad", LocalDateTime.now());
        ObjectMapperBuilder.build().copy(test);
    }

}