package io.github.factoryfx.factory.attribute;

import java.util.ArrayList;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringMapAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ValueMapAttributeTest {

    public static class ExampleMapFactory extends FactoryBase<Void,ExampleMapFactory> {
        public StringMapAttribute mapAttribute=new StringMapAttribute();
    }

    @Test
    public void testObservable(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleMapFactory.mapAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleMapFactory.mapAttribute.put("123","7787");

        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        exampleMapFactory.mapAttribute.get().put("123","7787");
        ObjectMapperBuilder.build().copy(exampleMapFactory);
    }
}