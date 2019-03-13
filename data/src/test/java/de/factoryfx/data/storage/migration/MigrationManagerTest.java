package de.factoryfx.data.storage.migration;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigrationManagerTest {

    @Test
    public void read_read(){
        MigrationManager<ExampleDataA,Void> manager =  new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });

        String data= ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA());
        ExampleDataA result = manager.read(data, createDataStorageMetadataDictionary());
        Assertions.assertNotNull(result);

    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionary() {
        return createDataStorageMetadataDictionary(new ExampleDataA());
    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionary(ExampleDataA exampleDataA) {
        exampleDataA.internal().addBackReferences();
        return exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
    }

    @Test
    public void test_renameAttribute() {

        String oldDictionary =   "{\n" +
                                 "  \"dataList\" : [ {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"wrongName\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceListAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceListAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataA\"\n" +
                                 "  }, {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"stringAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttributeC\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataB\"\n" +
                                 "  }, {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"stringAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataC\"\n" +
                                 "  } ]\n" +
                                 "}";

        String input =
                "{\n" +
                "  \"@class\" : \"de.factoryfx.data.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"wrongName\" : {\n" + //<======
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";


        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.renameAttribute(ExampleDataA.class, "wrongName",d->d.stringAttribute);
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("123",result.stringAttribute.get());
    }

    @Test
    public void read_renameClass() {

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceListAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.WrongNameExampleDataA\"\n" +//<======
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataB\"\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataC\"\n" +
                "  } ]\n" +
                "}";

        String input =
                "{\n" +
                "  \"@class\" : \"de.factoryfx.data.merge.testdata.WrongNameExampleDataA\",\n" +//<======
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"stringAttribute\" : {\n" +
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";


        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.renameClass("de.factoryfx.data.merge.testdata.WrongNameExampleDataA",ExampleDataA.class);
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("123",result.stringAttribute.get());
    }

    @Test
    public void read_removed_property_no_exception() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        DataStorageMetadataDictionary dummyDictionaryFromRoot = exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();

        String input =
                "{\n" +
                        "  \"@class\" : \"de.factoryfx.data.merge.testdata.ExampleDataA\",\n" +//<======
                        "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                        "  \"stringAttribute\" : {\n" +
                        "    \"v\" : \"123\"\n" +
                        "  },\n" +
                        "  \"removedAttribute\" : {\n" +
                        "    \"v\" : \"123\"\n" +
                        "  },\n" +
                        "  \"referenceAttribute\" : { },\n" +
                        "  \"referenceListAttribute\" : [ ]\n" +
                        "}";

        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        ExampleDataA result = manager.read(input,dummyDictionaryFromRoot);
        Assertions.assertEquals("123",result.stringAttribute.get());
    }

}