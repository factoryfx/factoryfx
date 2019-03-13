package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.CopySemantic;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.DataReferenceListAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataStorageMetadataTest {

    @Test
    public void test_getRemovedAttributes(){
        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataAPrevious.class).createDataStorageMetadata(1);
        previousDataStorageMetadata.renameClass(ExampleDataAPrevious.class.getName(),ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertTrue(previousDataStorageMetadata.isRemovedAttribute("garbage"));
    }

    @Test
    public void test_getRemovedAttributes_no_changes(){
        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataAPrevious.class).createDataStorageMetadata(1);
//        previousDataStorageMetadata.renameClass(ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertFalse(previousDataStorageMetadata.isRemovedAttribute("garbage"));
    }

    public static class ExampleDataALocal extends Data {
        public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");

    }

    @Test
    public void test_getRemovedAttributes_local_class(){
        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataALocal.class).createDataStorageMetadata(1);
//        previousDataStorageMetadata.renameClass(ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertFalse(previousDataStorageMetadata.isRemovedAttribute("stringAttribute"));
    }

    @Test
    public void test_attribute_rename(){
        DataStorageMetadata previousDataStorageMetadata = DataDictionary.getDataDictionary(ExampleDataA.class).createDataStorageMetadata(1);
        Assertions.assertNull(previousDataStorageMetadata.getAttribute("blub"));
        previousDataStorageMetadata.renameAttribute("stringAttribute","blub");
        Assertions.assertNotNull(previousDataStorageMetadata.getAttribute("blub"));
    }

}