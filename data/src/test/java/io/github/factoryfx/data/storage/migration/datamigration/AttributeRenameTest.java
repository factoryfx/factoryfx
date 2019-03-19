package io.github.factoryfx.data.storage.migration.datamigration;

import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import io.github.factoryfx.data.merge.testdata.ExampleDataA;
import io.github.factoryfx.data.storage.migration.MigrationManager;
import io.github.factoryfx.data.storage.migration.metadata.ExampleDataAPrevious;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttributeRenameTest {

    @Test
    public void test_rename_order_class_rename_1(){
        ExampleDataAPrevious exampleDataAPrevious = new ExampleDataAPrevious();
        exampleDataAPrevious.garbage.set("123");
        exampleDataAPrevious.internal().addBackReferences();

        MigrationManager<ExampleDataA,Void> dataMigrationManager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(),(root, oldDataStorageMetadataDictionary) -> { });
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

        MigrationManager<ExampleDataA,Void> dataMigrationManager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(),(root, oldDataStorageMetadataDictionary) -> {});
        dataMigrationManager.renameAttribute(ExampleDataA.class, "garbage", exampleDataA -> exampleDataA.stringAttribute);
        dataMigrationManager.renameClass(ExampleDataAPrevious.class.getName(),ExampleDataA.class);

        ExampleDataA exampleDataA = dataMigrationManager.migrate(ObjectMapperBuilder.build().writeValueAsTree(exampleDataAPrevious),exampleDataAPrevious.internal().createDataStorageMetadataDictionaryFromRoot());
        Assertions.assertEquals("123",exampleDataA.stringAttribute.get());
    }

}