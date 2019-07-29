package io.github.factoryfx.factory.storage;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import org.junit.jupiter.api.Test;

public class DataAndStoredMetadataTest {
    @Test
    public void test_json(){
        DataAndStoredMetadata<ExampleDataA> test = new DataAndStoredMetadata<>(new ExampleDataA(),new StoredDataMetadata("","","","",null,null,null));
        ObjectMapperBuilder.build().copy(test);
    }
}