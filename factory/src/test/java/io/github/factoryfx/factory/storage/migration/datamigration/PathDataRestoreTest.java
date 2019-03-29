package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;
import io.github.factoryfx.factory.storage.migration.datamigration.PathBuilder;
import io.github.factoryfx.factory.storage.migration.datamigration.PathDataRestore;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PathDataRestoreTest {

    @Test
    public void test() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(value);

        String input = ObjectMapperBuilder.build().writeValueAsString(exampleDataA);
        input = input.replace(
                "stringAttribute",
                "oldStringAttribute");

        exampleDataA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.renameAttribute(ExampleDataB.class.getName(),"stringAttribute","oldStringAttribute");

        PathDataRestore<ExampleDataA,String> pathDataRestore = new PathDataRestore<>(PathBuilder.of(String.class,"referenceAttribute.oldStringAttribute") ,(r, v)-> r.referenceAttribute.get().stringAttribute.set(v),ObjectMapperBuilder.build());

        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();
        ExampleDataA root = ObjectMapperBuilder.build().readValue(input, ExampleDataA.class);
        if (pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot)){
            pathDataRestore.migrate(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().readTree(input)), root);
        }
        Assertions.assertTrue(pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot));
        Assertions.assertEquals("1234",root.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void test_not_applied_for_no_changes() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(value);

        String input = ObjectMapperBuilder.build().writeValueAsString(exampleDataA);
//        input = input.replace(
//                "stringAttribute",
//                "oldStringAttribute");

        exampleDataA.internal().addBackReferences();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
//        dataStorageMetadataDictionaryFromRoot.renameAttribute(ExampleDataB.class.getName(),"stringAttribute","oldStringAttribute");

        PathDataRestore<ExampleDataA,String> pathDataRestore = new PathDataRestore<>(PathBuilder.of(String.class,"referenceAttribute.oldStringAttribute") ,(r, v)-> r.referenceAttribute.get().stringAttribute.set(v),ObjectMapperBuilder.build());

        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();
        ExampleDataA root = ObjectMapperBuilder.build().readValue(input, ExampleDataA.class);
        if (pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot)){
            pathDataRestore.migrate(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().readTree(input)), root);
        }
        Assertions.assertFalse(pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot));
        Assertions.assertEquals("1234",root.referenceAttribute.get().stringAttribute.get());
    }
}