package io.github.factoryfx.factory.attribute.types;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class StringMapAttributeTest {
    public static class ExampleMapFactory extends FactoryBase<Void, ExampleMapFactory> {
        public StringMapAttribute mapAttribute =new StringMapAttribute();
    }

    @Test
    public void test_json_inside_data(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        exampleMapFactory.mapAttribute.put("7787","dgdgf");
        ObjectMapperBuilder.build().copy(exampleMapFactory);
    }

    @Test
    public void test_json(){
        StringMapAttribute attribute = new StringMapAttribute();
        attribute.put("7787","dgdgf");
        StringMapAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals("dgdgf",copy.get("7787"));
    }

    @Test
    public void test_modify(){
        ExampleMapFactory exampleFactory = new ExampleMapFactory();
        exampleFactory.internal().finalise();
        exampleFactory.mapAttribute.put("7787","ddadad");
        Assertions.assertEquals(1,exampleFactory.getModified().size());
        Assertions.assertTrue(exampleFactory.getModified().contains(exampleFactory));
    }

    @Test
    public void test_resetModification(){
        ExampleMapFactory exampleFactory = new ExampleMapFactory();
        exampleFactory.mapAttribute.put("111","222");
        exampleFactory.internal().finalise();

        exampleFactory.mapAttribute.put("333","444");
        exampleFactory.internal().resetModificationFlat();
        Assertions.assertEquals(Map.of("111","222"),exampleFactory.mapAttribute.get());
    }



}