package de.factoryfx.data.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import de.factoryfx.data.merge.testdata.ExampleDataC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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


}