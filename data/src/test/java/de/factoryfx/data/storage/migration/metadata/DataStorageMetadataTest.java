package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataStorageMetadataTest {

    @Test
    public void test_getRemovedAttributes(){

        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataAPrevious.class).createDataStorageMetadata();
        DataStorageMetadata currentDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataA.class).createDataStorageMetadata();
        List<String> removedAttributes = currentDataStorageMetadata.getRemovedAttributes(previousDataStorageMetadata);
        Assert.assertEquals(1,removedAttributes.size());
            Assert.assertEquals("garbage",removedAttributes.get(0));
    }

    @Test
    public void test_getRemovedAttributes_no_changes(){

        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataA.class).createDataStorageMetadata();
        DataStorageMetadata currentDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataA.class).createDataStorageMetadata();
        List<String> removedAttributes = currentDataStorageMetadata.getRemovedAttributes(previousDataStorageMetadata);
        Assert.assertEquals(0,removedAttributes.size());
    }

}