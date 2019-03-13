package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import de.factoryfx.data.storage.migration.metadata.ExampleDataAPrevious;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AttributeRenameTest {

    @Test
    public void test_rename_order_class_rename_1(){
        ExampleDataAPrevious exampleDataAPrevious = new ExampleDataAPrevious();
        exampleDataAPrevious.garbage.set("123");
        exampleDataAPrevious.internal().addBackReferences();

        DataMigrationManager<ExampleDataA> dataMigrationManager = new DataMigrationManager<>((root, oldDataStorageMetadataDictionary) -> { }, ExampleDataA.class);
        dataMigrationManager.renameClass(ExampleDataAPrevious.class.getName(),ExampleDataA.class);
        dataMigrationManager.renameAttribute(ExampleDataA.class, "garbage", exampleDataA -> exampleDataA.stringAttribute);

        ExampleDataA exampleDataA = dataMigrationManager.migrate(ObjectMapperBuilder.build().writeValueAsTree(exampleDataAPrevious),exampleDataAPrevious.internal().createDataStorageMetadataDictionaryFromRoot());
        Assertions.assertEquals("123",exampleDataA.stringAttribute.get());
    }

    @Test
    public void test_rename_order_class_rename_2(){
        ExampleDataAPrevious exampleDataAPrevious = new ExampleDataAPrevious();
        exampleDataAPrevious.garbage.set("123");
        exampleDataAPrevious.internal().addBackReferences();

        DataMigrationManager<ExampleDataA> dataMigrationManager = new DataMigrationManager<>((root, oldDataStorageMetadataDictionary) -> {}, ExampleDataA.class);
        dataMigrationManager.renameAttribute(ExampleDataA.class, "garbage", exampleDataA -> exampleDataA.stringAttribute);
        dataMigrationManager.renameClass(ExampleDataAPrevious.class.getName(),ExampleDataA.class);

        ExampleDataA exampleDataA = dataMigrationManager.migrate(ObjectMapperBuilder.build().writeValueAsTree(exampleDataAPrevious),exampleDataAPrevious.internal().createDataStorageMetadataDictionaryFromRoot());
        Assertions.assertEquals("123",exampleDataA.stringAttribute.get());
    }

}