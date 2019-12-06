package io.github.factoryfx.factory.storage.migration;

import io.github.factoryfx.factory.attribute.ValueMapAttributeTest;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.storage.migration.datamigration.PathBuilder;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigrationManagerTest {

    @Test
    public void read_read(){
        MigrationManager<ExampleDataA> manager =  new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });

        String data= ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA());
        ExampleDataA result = manager.read(data, createDataStorageMetadataDictionary());
        Assertions.assertNotNull(result);

    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionary() {
        return createDataStorageMetadataDictionary(new ExampleDataA());
    }

    private DataStorageMetadataDictionary createDataStorageMetadataDictionary(ExampleDataA exampleDataA) {
        exampleDataA.internal().finalise();
        return exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot();
    }

    @Test
    public void test_renameAttribute() {


        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"wrongName\",\n" +//<======
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =
                "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"wrongName\" : {\n" +//<======
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";


        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
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
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.WrongNameExampleDataA\",\n" +//<======
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =
                "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.WrongNameExampleDataA\",\n" +//<======
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"stringAttribute\" : {\n" +
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";


        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.renameClass("io.github.factoryfx.factory.merge.testdata.WrongNameExampleDataA",ExampleDataA.class);
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("123",result.stringAttribute.get());
    }

    @Test
    public void read_removed_property_no_exception() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"removedAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =
                "{\n" +
                        "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +//<======
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

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("123",result.stringAttribute.get());
    }



    @Test
    public void read_retype() {

        String oldDictionary =   "{\n" +
                                    "  \"dataList\" : [ {\n" +
                                    "    \"attributes\" : [ {\n" +
                                    "      \"variableName\" : \"stringAttribute\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.BooleanAttribute\"\n" +
                                    "    }, {\n" +
                                    "      \"variableName\" : \"referenceAttribute\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                                    "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                                    "    }, {\n" +
                                    "      \"variableName\" : \"referenceListAttribute\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                                    "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                                    "    } ],\n" +
                                    "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                                    "    \"count\" : 1\n" +
                                    "  }, {\n" +
                                    "    \"attributes\" : [ {\n" +
                                    "      \"variableName\" : \"stringAttribute\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                                    "    }, {\n" +
                                    "      \"variableName\" : \"referenceAttribute\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                                    "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                                    "    }, {\n" +
                                    "      \"variableName\" : \"referenceAttributeC\",\n" +
                                    "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                                    "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                                    "    } ],\n" +
                                    "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                                    "    \"count\" : 1\n" +
                                    "  } ],\n" +
                                    "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                                    "}";

        String input =  "{\n" +
                        "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                        "  \"id\" : \"c656f1f2-b968-6c13-e47d-bde5a6b1a681\",\n" +
                        "  \"treeBuilderClassUsed\" : false,\n" +
                        "  \"referenceAttribute\" : { },\n" +
                        "  \"stringAttribute\" : {\n" +
//<--- wrong type
                        "    \"v\" : false\n"+
                        "  },\n" +
                        "  \"referenceListAttribute\" : [ ]\n" +
                        "}";
        
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());
        exampleDataA.internal().finalise();

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.restoreAttribute(Boolean.class, new PathBuilder<Boolean>().attribute("stringAttribute"),(r,v)->{
            r.stringAttribute.set(v?"1":"0");
        });
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("0",result.stringAttribute.get());
    }

    @Test
    public void read_retype_ref() {

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.BooleanAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +//<---
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =  "{\n" +
                        "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                        "  \"id\" : \"b663aa09-1106-7da5-f696-90831cb670ca\",\n" +
                        "  \"treeBuilderClassUsed\" : false,\n" +
                        "  \"stringAttribute\" : { },\n" +
                        "  \"referenceAttribute\" : {\n" +
                        "    \"v\" : {\n" +
                        "      \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" + //<==wrong type
                        "      \"id\" : \"02a4f1cb-1b1d-5830-cbaf-10363863a385\",\n" +
                        "      \"treeBuilderClassUsed\" : false,\n" +
                        "      \"stringAttribute\" : { \"v\": \"test123\"},\n" +
                        "      \"referenceAttribute\" : { },\n" +
                        "      \"referenceListAttribute\" : [ ]\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"referenceListAttribute\" : [ ]\n" +
                        "}";

        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());
        exampleDataA.internal().finalise();
//        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleDataA));

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.restoreAttribute(ExampleDataA.class, new PathBuilder<ExampleDataA>().attribute("referenceAttribute"),(r,v)->{
            ExampleDataB factory = new ExampleDataB();
            factory.stringAttribute.set(v.stringAttribute.get());
            r.referenceAttribute.set(factory);
        });
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("test123",result.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void read_retype_normal_to_ref() {

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.BooleanAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +//<---
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =  "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"b663aa09-1106-7da5-f696-90831cb670ca\",\n" +
                "  \"treeBuilderClassUsed\" : false,\n" +
                "  \"stringAttribute\" : { },\n" +
                "  \"referenceAttribute\" : {\n" +
                "    \"v\" : \"bla\"" +//<== wrong type
                "    }\n" +
                "  },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";

        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceAttribute.set(new ExampleDataB());
        exampleDataA.internal().finalise();
//        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleDataA));

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.restoreAttribute(String.class, new PathBuilder<String>().attribute("referenceAttribute"),(r,v)->{
            ExampleDataB factory = new ExampleDataB();
            factory.stringAttribute.set(v);
            r.referenceAttribute.set(factory);
        });
        ExampleDataA result = manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
        Assertions.assertEquals("bla",result.referenceAttribute.get().stringAttribute.get());
    }

    @Test
    public void read_removed_class_no_exception() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"removedAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataBremoved\",\n" +//<==removed type
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =  "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"b663aa09-1106-7da5-f696-90831cb670ca\",\n" +
                "  \"treeBuilderClassUsed\" : false,\n" +
                "  \"stringAttribute\" : { },\n" +
                "  \"referenceAttribute\" : {\n" +
                "    \"v\" : {\n" +
                "      \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataBremoved\",\n" + //<==removed type
                "      \"id\" : \"02a4f1cb-1b1d-5830-cbaf-10363863a385\",\n" +
                "      \"treeBuilderClassUsed\" : false,\n" +
                "      \"stringAttribute\" : { \"v\": \"test123\"},\n" +
                "      \"referenceAttribute\" : { },\n" +
                "      \"referenceListAttribute\" : [ ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.read(input,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class));
    }


    @Test
    public void read_removed_restore() {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().finalise();

        String oldDictionary =   "{\n" +
                "  \"dataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"removedAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "    \"count\" : 1\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"io.github.factoryfx.factory.attribute.dependency.FactoryAttribute\",\n" +
                "      \"referenceClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\"\n" +
                "    } ],\n" +
                "    \"className\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataBremoved\",\n" +//<==removed type
                "    \"count\" : 1\n" +
                "  } ],\n" +
                "  \"rootClass\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\"\n" +
                "}";

        String input =  "{\n" +
                "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"b663aa09-1106-7da5-f696-90831cb670ca\",\n" +
                "  \"treeBuilderClassUsed\" : false,\n" +
                "  \"stringAttribute\" : { },\n" +
                "  \"referenceAttribute\" : {\n" +
                "    \"v\" : {\n" +
                "      \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataBremoved\",\n" + //<==removed type
                "      \"id\" : \"02a4f1cb-1b1d-5830-cbaf-10363863a385\",\n" +
                "      \"treeBuilderClassUsed\" : false,\n" +
                "      \"stringAttribute\" : { \"v\": \"test123\"},\n" +
                "      \"referenceAttribute\" : { },\n" +
                "      \"referenceListAttribute\" : [ ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";

        MigrationManager<ExampleDataA> manager = new MigrationManager<>(ExampleDataA.class, ObjectMapperBuilder.build(), (root1, oldDataStorageMetadataDictionary) -> { });
        manager.restoreAttribute(String.class,PathBuilder.of("referenceAttribute.stringAttribute"),(root, value) -> {
            root.stringAttribute.set(value);
        });
        ExampleDataA read = manager.read(input, ObjectMapperBuilder.build().readValue(oldDictionary, DataStorageMetadataDictionary.class));
        Assertions.assertEquals("test123",read.stringAttribute.get());
    }

}