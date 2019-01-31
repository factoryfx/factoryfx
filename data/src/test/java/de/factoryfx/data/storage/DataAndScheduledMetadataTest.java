package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Test;

public class DataAndScheduledMetadataTest {

    @Test
    public void test_json(){
        DataAndScheduledMetadata<ExampleDataA,Void> test = new DataAndScheduledMetadata<>(new ExampleDataA(),new ScheduledDataMetadata<>(null,"","","","",null,null,null,null));
        ObjectMapperBuilder.build().copy(test);
    }

}