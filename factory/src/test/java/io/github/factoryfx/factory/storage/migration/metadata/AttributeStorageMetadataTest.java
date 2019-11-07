package io.github.factoryfx.factory.storage.migration.metadata;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeStorageMetadataTest {

    @Test
    public void test_json(){
        AttributeStorageMetadata attributeStorageMetadataTest = new AttributeStorageMetadata("variableName","attributeClassName","referenceClass");
        AttributeStorageMetadata copy  = ObjectMapperBuilder.build().copy(attributeStorageMetadataTest);

        assertEquals("variableName",copy.variableName);
        assertEquals("attributeClassName",copy.attributeClassName);
        assertEquals("referenceClass",copy.referenceClass);

    }

}