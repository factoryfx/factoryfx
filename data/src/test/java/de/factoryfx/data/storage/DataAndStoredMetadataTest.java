package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataAndStoredMetadataTest {
    @Test
    public void test_json(){
        DataAndStoredMetadata<ExampleDataA> test = new DataAndStoredMetadata<>(new ExampleDataA(),new StoredDataMetadata());
        ObjectMapperBuilder.build().copy(test);
    }
}