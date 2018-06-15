package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Test;

public class DataAndNewMetadataTest {

    @Test
    public void test_json(){
        DataAndNewMetadata<ExampleDataA> test = new DataAndNewMetadata<>(new ExampleDataA(),new NewDataMetadata());
        ObjectMapperBuilder.build().copy(test);
    }

}