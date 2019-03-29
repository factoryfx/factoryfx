package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DataAndScheduledMetadataTest {

    @Test
    public void test_json(){
        ScheduledUpdate<ExampleDataA> test = new ScheduledUpdate<>(new ExampleDataA(),"fg","dsad","dad", LocalDateTime.now());
        ObjectMapperBuilder.build().copy(test);
    }

}