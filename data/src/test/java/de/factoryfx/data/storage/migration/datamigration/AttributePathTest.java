package de.factoryfx.data.storage.migration.datamigration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.merge.testdata.ExampleDataB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttributePathTest {

    @Test
    public void test_resolve_string(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(exampleDataB);

        Assertions.assertEquals("1234",PathBuilder.value(String.class).pathElement("referenceAttribute").attribute("stringAttribute").resolve(new DataJsonNode((ObjectNode)ObjectMapperBuilder.build().writeValueAsTree(exampleDataA))));
    }

    @Test
    public void test_resolve_string_root_attribute(){
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("1234");
        Assertions.assertEquals("1234",PathBuilder.value(String.class).attribute("stringAttribute").resolve(new DataJsonNode((ObjectNode)ObjectMapperBuilder.build().writeValueAsTree(exampleDataA))));
    }

    @Test
    public void test_resolve_ref(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.stringAttribute.set("1234");
        exampleDataA.referenceAttribute.set(exampleDataB);


        ExampleDataB referenceAttribute = PathBuilder.value(ExampleDataB.class).attribute("referenceAttribute").resolve(new DataJsonNode((ObjectNode) ObjectMapperBuilder.build().writeValueAsTree(exampleDataA)));
        Assertions.assertEquals("1234", referenceAttribute.stringAttribute.get());
    }

}