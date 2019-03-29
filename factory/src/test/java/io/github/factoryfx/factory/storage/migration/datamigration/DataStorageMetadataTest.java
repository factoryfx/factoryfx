package io.github.factoryfx.factory.storage.migration.datamigration;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadata;
import io.github.factoryfx.factory.storage.migration.metadata.ExampleDataAPrevious;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DataStorageMetadataTest {

    @Test
    public void test_getRemovedAttributes(){
        DataStorageMetadata previousDataStorageMetadata = FactoryMetadataManager.getMetadata(ExampleDataAPrevious.class).createDataStorageMetadata(1);
        previousDataStorageMetadata.renameClass(ExampleDataAPrevious.class.getName(), ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertTrue(previousDataStorageMetadata.isRemovedAttribute("garbage"));
    }

    @Test
    public void test_getRemovedAttributes_no_changes(){
        DataStorageMetadata previousDataStorageMetadata = FactoryMetadataManager.getMetadata(ExampleDataAPrevious.class).createDataStorageMetadata(1);
//        previousDataStorageMetadata.renameClass(ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertFalse(previousDataStorageMetadata.isRemovedAttribute("garbage"));
    }

    public static class ExampleDataALocal extends FactoryBase<Void, ExampleDataALocal> {
        public final StringAttribute stringAttribute = new StringAttribute().labelText("ExampleA1");

    }

    @Test
    public void test_getRemovedAttributes_local_class(){
        DataStorageMetadata previousDataStorageMetadata = FactoryMetadataManager.getMetadata(ExampleDataALocal.class).createDataStorageMetadata(1);
//        previousDataStorageMetadata.renameClass(ExampleDataA.class.getName());
        previousDataStorageMetadata.markRemovedAttributes();

        Assertions.assertFalse(previousDataStorageMetadata.isRemovedAttribute("stringAttribute"));
    }

    @Test
    public void test_attribute_rename(){
        DataStorageMetadata previousDataStorageMetadata = FactoryMetadataManager.getMetadata(ExampleDataA.class).createDataStorageMetadata(1);
        Assertions.assertNull(previousDataStorageMetadata.getAttribute("blub"));
        previousDataStorageMetadata.renameAttribute("stringAttribute","blub");
        Assertions.assertNotNull(previousDataStorageMetadata.getAttribute("blub"));
    }

}