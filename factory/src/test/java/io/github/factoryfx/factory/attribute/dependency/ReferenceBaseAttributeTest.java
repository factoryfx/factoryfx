package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReferenceBaseAttributeTest {

    public static class GenericData<T> extends FactoryBase<Void, GenericData<T>> {

    }

    @Test
    public void test_setupunsafe(){
        FactoryReferenceAttribute<GenericData<String>,Void,GenericData<String>> test1 = new FactoryReferenceAttribute<>();
    }

    @Test
    public void test_AttributeStorageMetadata(){
        FactoryReferenceAttribute<GenericData<String>,Void,GenericData<String>> test1 = new FactoryReferenceAttribute<>();
        AttributeStorageMetadata attributeStorageMetadata = test1.createAttributeStorageMetadata("bla");
        AttributeStorageMetadata attributeStorageMetadata2 = test1.createAttributeStorageMetadata("bla");
        Assertions.assertTrue(attributeStorageMetadata.isReference());
        Assertions.assertTrue(attributeStorageMetadata.match(attributeStorageMetadata2));
    }


}