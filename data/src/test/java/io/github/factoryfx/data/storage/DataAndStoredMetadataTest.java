package io.github.factoryfx.data.storage;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

public class DataAndStoredMetadataTest {
    @Test
    public void test_json(){
        DataAndStoredMetadata<ExampleDataA,Void> test = new DataAndStoredMetadata<>(new ExampleDataA(),new StoredDataMetadata<>("","","","",null,null));
        ObjectMapperBuilder.build().copy(test);
    }
}