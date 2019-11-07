package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinter;
import io.github.factoryfx.factory.testfactories.poly.ErrorPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.OutPrinterFactory;
import io.github.factoryfx.factory.testfactories.poly.Printer;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;


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

        exampleDataA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.renameAttribute(ExampleDataB.class.getName(),"stringAttribute","oldStringAttribute");

        PathDataRestore<ExampleDataA,String> pathDataRestore = new PathDataRestore<>(PathBuilder.of("referenceAttribute.oldStringAttribute") ,(r, v)-> r.referenceAttribute.get().stringAttribute.set(v),new AttributeValueParser<>(ObjectMapperBuilder.build(),String.class));

        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();
        ExampleDataA root = ObjectMapperBuilder.build().readValue(input, ExampleDataA.class);
        if (pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA))){
            pathDataRestore.migrate(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().readTree(input)), root);
        }
        Assertions.assertTrue(pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA)));
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

        exampleDataA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
//        dataStorageMetadataDictionaryFromRoot.renameAttribute(ExampleDataB.class.getName(),"stringAttribute","oldStringAttribute");

        PathDataRestore<ExampleDataA,String> pathDataRestore = new PathDataRestore<>(PathBuilder.of("referenceAttribute.oldStringAttribute") ,(r, v)-> r.referenceAttribute.get().stringAttribute.set(v),new AttributeValueParser<>(ObjectMapperBuilder.build(),String.class));

        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();
        ExampleDataA root = ObjectMapperBuilder.build().readValue(input, ExampleDataA.class);
        if (pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA))){
            pathDataRestore.migrate(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().readTree(input)), root);
        }
        Assertions.assertFalse(pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA)));
        Assertions.assertEquals("1234",root.referenceAttribute.get().stringAttribute.get());
    }

    private DataJsonNode createDataJsonNode(FactoryBase<?,?> factory){
        return new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().writeValueAsTree(factory));

    }

    @Test
    public void test_list() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.stringAttribute.set("1234");
        exampleDataA.referenceListAttribute.add(new ExampleDataB());
        exampleDataA.referenceListAttribute.add(new ExampleDataB());

        String input = ObjectMapperBuilder.build().writeValueAsString(exampleDataA);
        input = input.replace(
                "referenceListAttribute",
                "oldReferenceListAttribute");

        exampleDataA.internal().finalise();
        DataStorageMetadataDictionary dataStorageMetadataDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
        dataStorageMetadataDictionaryFromRoot.renameAttribute(ExampleDataA.class.getName(),"referenceListAttribute","oldReferenceListAttribute");

        PathDataRestore<ExampleDataA, List<ExampleDataB>> pathDataRestore = new PathDataRestore<>(PathBuilder.of("oldReferenceListAttribute") ,(r, v)-> r.referenceListAttribute.addAll(v), new AttributeValueListParser<>(new AttributeValueParser<>(ObjectMapperBuilder.build(), ExampleDataB.class)));

        dataStorageMetadataDictionaryFromRoot.markRemovedAttributes();
        ExampleDataA root = ObjectMapperBuilder.build().readValue(input, ExampleDataA.class);
        if (pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA))){
            pathDataRestore.migrate(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().readTree(input)), root);
        }
        Assertions.assertTrue(pathDataRestore.canMigrate(dataStorageMetadataDictionaryFromRoot, createDataJsonNode(exampleDataA)));
        Assertions.assertEquals(2 ,root.referenceListAttribute.get().size());
    }
}