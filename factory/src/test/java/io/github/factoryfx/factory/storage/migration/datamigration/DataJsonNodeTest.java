package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AssertionsKt$sam$i$org_junit_jupiter_api_function_Executable$0;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class DataJsonNodeTest {

    @Test
    public void test_getChildrenFromRoot_simple() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        exampleDataA.referenceAttribute.set(value);
        value.referenceAttributeC.set(new ExampleDataC());
        exampleDataA.referenceListAttribute.add(new ExampleDataB());


        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(exampleDataA);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);

        List<DataJsonNode> childrenFromRoot = dataJsonNode.collectChildrenFromRoot();
        Assertions.assertEquals(4, childrenFromRoot.size());
    }

    @Test
    public void test_getChildrenFromRoot_loop() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        exampleDataA.referenceAttribute.set(value);
        value.referenceAttribute.set(exampleDataA);

        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(exampleDataA);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);

        List<DataJsonNode> childrenFromRoot = dataJsonNode.collectChildrenFromRoot();
        Assertions.assertEquals(2, childrenFromRoot.size());
    }

    @Test
    public void test_getChildrenFromRoot_ref() {

        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        exampleDataA.referenceAttribute.set(value);
        value.referenceAttributeC.set(new ExampleDataC());
        exampleDataA.referenceListAttribute.add(value);


        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(exampleDataA);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);

        List<DataJsonNode> childrenFromRoot = dataJsonNode.collectChildrenFromRoot();
        Assertions.assertEquals(3, childrenFromRoot.size());
    }

    @Test
    public void test_getChildrenFromRoot_id() {
        ExampleDataA exampleDataA = new ExampleDataA();
        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(exampleDataA);
        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        Assertions.assertEquals(exampleDataA.getId().toString(), dataJsonNode.getId());
    }


    public static class IterationTestFactory extends FactoryBase<Void, IterationTestFactory> {
        public static final List<String> createOrder= new ArrayList<>();
        public String testinfo;
        public final FactoryListAttribute<IterationTestFactory,Void,IterationTestFactory> children = new FactoryListAttribute<>();

        public IterationTestFactory(String testinfo) {
            this();
            this.testinfo=testinfo;
        }

        public IterationTestFactory() {
            super();
            createOrder.add(testinfo);
        }

        @JsonSetter
        public void setTestinfo(String testinfo){
            this.testinfo=testinfo;
            createOrder.add(testinfo);
        }

    }

//        h
//      / | \
//     /  e  \
//    d       g
//   /|\      |
//  / | \     f
// a  b  c
    //fixid order order: hdabcegf
    @Test
    public void test_jackson_iteration_oder_match_idfix_order() {
        IterationTestFactory root = new IterationTestFactory("h");

        IterationTestFactory d = new IterationTestFactory("d");
        root.children.add(d);
        root.children.add(new IterationTestFactory("e"));
        IterationTestFactory g = new IterationTestFactory("g");
        root.children.add(g);

        d.children.add(new IterationTestFactory("a"));
        d.children.add(new IterationTestFactory("b"));
        d.children.add(new IterationTestFactory("c"));

        g.children.add(new IterationTestFactory("f"));

        IterationTestFactory.createOrder.clear();
        System.out.println( ObjectMapperBuilder.build().writeValueAsString(root));
        ObjectMapperBuilder.build().copy(root);


        Assertions.assertEquals("hdabcegf",IterationTestFactory.createOrder.stream().filter(Objects::nonNull).collect(Collectors.joining("")));
    }


    @Test
    public void test_fixIdsDeep_smoketest() {

        ExampleDataA root = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        root.referenceAttribute.set(value);
        value.referenceAttributeC.set(new ExampleDataC());
        root.referenceListAttribute.add(new ExampleDataB());
        root.internal().finalise();

        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(root);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        dataJsonNode.fixIdsDeepFromRoot(root.internal().createDataStorageMetadataDictionaryFromRoot());
    }

    @Test
    public void test_fixIdsDeep_null_ref_no_exception() {

        ExampleDataA root = new ExampleDataA();
        root.referenceAttribute.set(null);
        root.internal().finalise();

        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(root);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        dataJsonNode.fixIdsDeepFromRoot(root.internal().createDataStorageMetadataDictionaryFromRoot());

    }

    @Test
    public void test_fixIdsDeep_ref() {
        ExampleDataA root = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        root.referenceAttribute.set(exampleDataB);
        root.referenceListAttribute.add(exampleDataB);

        root.internal().finalise();


        String json=
                "{\n" +
                        "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                        "  \"id\" : \"445a7f33-f166-dd28-9dc5-4ebbaa2c5e17\",\n" +
                        "  \"stringAttribute\" : { },\n" +
                        "  \"referenceAttribute\" : {\n" +
                        "    \"v\" : \"5d5dc859-9b94-1e8d-1937-45caf0d304e4\"" +
                        "  },\n" +
                        "  \"referenceListAttribute\" : [ "+
                                        "{\n" +
                                        "      \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                                        "      \"id\" : \"5d5dc859-9b94-1e8d-1937-45caf0d304e4\",\n" +
                                        "      \"stringAttribute\" : { },\n" +
                                        "      \"referenceAttribute\" : { },\n" +
                                        "      \"referenceAttributeC\" : { }\n" +
                                        "}\n" +
                        " ]\n" +
                        "}";
        Assertions.assertThrows(RuntimeException.class, () -> {
            ObjectMapperBuilder.build().readValue(json,ExampleDataA.class);
        });


        JsonNode jsonNode = ObjectMapperBuilder.build().readTree(json);
        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        dataJsonNode.fixIdsDeepFromRoot(root.internal().createDataStorageMetadataDictionaryFromRoot());



        ObjectMapperBuilder.build().treeToValue(jsonNode,ExampleDataA.class);
    }

    @Test
    public void test_fixIdsDeep_List() {
        ExampleDataA root = new ExampleDataA();
        root.referenceListAttribute.add(new ExampleDataB());
        root.internal().finalise();


        String json=
                "{\n" + "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"a7697a9c-41f0-c839-1b9d-198d021a13b5\",\n" + "  \"stringAttribute\" : { },\n" +
                "  \"referenceAttribute\" : { },\n" + "  \"referenceListAttribute\" : [ \"3502ce98-8011-81f9-f767-bf2903702b6e\", {\n" +
                "    \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                "    \"id\" : \"3502ce98-8011-81f9-f767-bf2903702b6e\",\n" +
                "    \"stringAttribute\" : { },\n" +
                "    \"referenceAttribute\" : { },\n" +
                "    \"referenceAttributeC\" : { }\n" +
                "  } ]\n" +
                "}";
        Assertions.assertThrows(RuntimeException.class, () -> {
            ObjectMapperBuilder.build().readValue(json,ExampleDataA.class);
        });


        JsonNode jsonNode = ObjectMapperBuilder.build().readTree(json);
        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        dataJsonNode.fixIdsDeepFromRoot(root.internal().createDataStorageMetadataDictionaryFromRoot());

        ObjectMapperBuilder.build().treeToValue(jsonNode,ExampleDataA.class);
    }

    @Test
    public void test_getAttributes() {
        ExampleDataA root = new ExampleDataA();
        root.referenceAttribute.set(null);
        root.internal().finalise();

        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(root);
        System.out.println(ObjectMapperBuilder.build().writeValueAsString(jsonNode));

        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        Assertions.assertEquals(3,dataJsonNode.getAttributes().size());
    }

    @Test
    public void test_getAttributes_only_attributes() {
        ExampleDataA root = new ExampleDataA();
        root.getId();
        root.internal().setTreeBuilderName("12345");
        root.internal().finalise();
        JsonNode jsonNode = ObjectMapperBuilder.build().writeValueAsTree(root);
        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        Assertions.assertEquals(3,dataJsonNode.getAttributes().size());
    }


    @Test
    public void test_fixIdsDeep_nested() {
        ExampleDataA root = new ExampleDataA();
        {
            ExampleDataB exampleDataB = new ExampleDataB();
            exampleDataB.referenceAttributeC.set(new ExampleDataC());
            root.referenceAttribute.set(exampleDataB);
        }
        {
            ExampleDataB exampleDataB = new ExampleDataB();
            exampleDataB.referenceAttributeC.set(new ExampleDataC());
            root.referenceListAttribute.add(exampleDataB);
        }

        root.internal().finalise();


//        System.out.println(ObjectMapperBuilder.build().writeValueAsString(root));


        //referenceAttributeC is id bugged
        String json=
                "{\n" +
                        "  \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataA\",\n" +
                        "  \"id\" : \"d864a939-5fc3-a38a-7d11-897bd6ce1c2d\",\n" +
                        "  \"stringAttribute\" : { },\n" +
                        "  \"referenceAttribute\" : {\n" +
                        "    \"v\" : {\n" +
                        "      \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                        "      \"id\" : \"f62b2346-43a6-2c5d-4246-580c4739eb89\",\n" +
                        "      \"stringAttribute\" : { },\n" +
                        "      \"referenceAttribute\" : { },\n" +
                        "      \"referenceAttributeC\" : {\n" +
                        "        \"v\" : \"608c45f0-6ad4-6e54-f9eb-726c29326a44\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"referenceListAttribute\" : [ {\n" +
                        "    \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataB\",\n" +
                        "    \"id\" : \"bbf51bf1-a35a-d23c-ddfc-4dabe8efcd03\",\n" +
                        "    \"stringAttribute\" : { },\n" +
                        "    \"referenceAttribute\" : { },\n" +
                        "    \"referenceAttributeC\" : {\n" +
                        "      \"v\" : {\n" +
                        "        \"@class\" : \"io.github.factoryfx.factory.merge.testdata.ExampleDataC\",\n" +
                        "        \"id\" : \"608c45f0-6ad4-6e54-f9eb-726c29326a44\",\n" +
                        "        \"stringAttribute\" : { }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  } ]\n" +
                        "}";
        Assertions.assertThrows(RuntimeException.class, () -> {
            ObjectMapperBuilder.build().readValue(json,ExampleDataA.class);
        });


        JsonNode jsonNode = ObjectMapperBuilder.build().readTree(json);
        DataJsonNode dataJsonNode = new DataJsonNode((ObjectNode) jsonNode);
        dataJsonNode.fixIdsDeepFromRoot(root.internal().createDataStorageMetadataDictionaryFromRoot());



        ObjectMapperBuilder.build().treeToValue(jsonNode,ExampleDataA.class);
    }



}