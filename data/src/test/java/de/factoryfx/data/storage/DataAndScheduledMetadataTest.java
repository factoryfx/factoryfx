package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Test;

public class DataAndScheduledMetadataTest {

    @Test
    public void test_json(){
        DataAndScheduledMetadata<ExampleDataA,Void> test = new DataAndScheduledMetadata<>(new ExampleDataA(),new ScheduledDataMetadata<>(null,"","","","",0,null,null));
        ObjectMapperBuilder.build().copy(test);
    }

}