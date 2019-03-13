package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

public class DataAndStoredMetadataTest {
    @Test
    public void test_json(){
        DataAndStoredMetadata<ExampleDataA,Void> test = new DataAndStoredMetadata<>(new ExampleDataA(),new StoredDataMetadata<>("","","","",null,null));
        ObjectMapperBuilder.build().copy(test);
    }
}