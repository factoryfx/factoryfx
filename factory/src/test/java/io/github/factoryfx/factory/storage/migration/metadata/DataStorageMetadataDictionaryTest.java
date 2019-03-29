package io.github.factoryfx.factory.storage.migration.metadata;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
        Assertions.assertEquals(ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()),ObjectMapperBuilder.build().writeValueAsString(createDataStorageMetadataDictionaryFromRoot()));
    }

    @Test
    public void test_init(){
        Assertions.assertEquals(1, createDataStorageMetadataDictionaryFromRoot().dataList.size());
    }

    @Test
    public void test_rename_root(){
        DataStorageMetadataDictionary dictionary = createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass(ExampleDataA.class.getName(),"a.b.C");
        Assertions.assertEquals("a.b.C",dictionary.rootClass);
    }

    @Test
    public void test_rename_attributeref(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());
        exampleDataA.internal().addBackReferences();
        DataStorageMetadataDictionary dictionary  = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        dictionary.renameClass(ExampleDataB.class.getName(),"a.b.C");
        Assertions.assertEquals("a.b.C",dictionary.getDataStorageMetadata("a.b.C").getClassName());
        Assertions.assertEquals("a.b.C",dictionary.getDataStorageMetadata(ExampleDataA.class.getName()).getAttribute("referenceAttribute").referenceClass);
    }
}