package io.github.factoryfx.data.attribute;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.storage.migration.metadata.AttributeStorageMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReferenceBaseAttributeTest {

    public static class GenericData<T> extends Data {

    }

    @Test
    public void test_setupunsafe(){
        DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(GenericData.class,null);
    }

    @Test
    public void test_setupunsafe_wrongclass(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(Object.class, null);
        });
    }

    @Test
    public void test_AttributeStorageMetadata(){
        DataReferenceAttribute<GenericData<String>> test1 = new DataReferenceAttribute<>(GenericData.class,null);
        AttributeStorageMetadata attributeStorageMetadata = test1.createAttributeStorageMetadata("bla");
        AttributeStorageMetadata attributeStorageMetadata2 = test1.createAttributeStorageMetadata("bla");
        Assertions.assertTrue(attributeStorageMetadata.isReference());
        Assertions.assertTrue(attributeStorageMetadata.match(attributeStorageMetadata2));
    }


}