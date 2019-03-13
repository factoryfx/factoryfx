package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeStorageMetadataTest {

    @Test
    public void test_json(){
        AttributeStorageMetadata attributeStorageMetadataTest = new AttributeStorageMetadata("variableName","attributeClassName",true,"referenceClass");
        AttributeStorageMetadata copy  = ObjectMapperBuilder.build().copy(attributeStorageMetadataTest);

        Assertions.assertEquals("variableName",copy.variableName);
        Assertions.assertEquals("attributeClassName",copy.attributeClassName);
        assertTrue(copy.isReference);
        Assertions.assertEquals("referenceClass",copy.referenceClass);

    }

}