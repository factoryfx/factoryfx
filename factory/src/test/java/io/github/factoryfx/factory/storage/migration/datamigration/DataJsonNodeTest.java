package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.storage.migration.datamigration.DataJsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        Assertions.assertEquals(exampleDataA.getId(), dataJsonNode.getId());
    }


    public static class IterationTestFactory extends FactoryBase<Void, IterationTestFactory> {
        public static final List<String> createOrder= new ArrayList<>();
        public String testinfo;
        public final FactoryReferenceListAttribute<IterationTestFactory,Void,IterationTestFactory> children = new FactoryReferenceListAttribute<>();

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
    public void test_jackosn_iteration_oder_match_idfix_order() {
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

}