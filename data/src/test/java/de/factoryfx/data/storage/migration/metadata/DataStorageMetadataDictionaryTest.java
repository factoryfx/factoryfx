package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;


public class DataStorageMetadataDictionaryTest {

    @Test
    public void test_json(){
        ObjectMapperBuilder.build().copy(createDataStorageMetadataDictionaryFromRoot());
//        System.out.println( ObjectMapperBuilder.build().writeValueAsString(new DataStorageMetadataDictionary(ExampleDataA.class)));
    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionaryFromRoot() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        return exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
    }

    @Test
    public void test_json_stable(){
        Assert.assertEquals(ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()),ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()));
    }

    @Test
    public void test_init(){
        Assert.assertEquals(1, createDataStorageMetadataDictionaryFromRoot().dataStorageMetadataList.size());
    }
}