package io.github.factoryfx.factory.attribute.types;

import java.util.ArrayList;
import java.util.List;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringListAttributeTest {

    @Test
    public void test_json(){
        StringListAttribute attribute = new StringListAttribute();
        String value = "123";
        attribute.add(value);
        StringListAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get().get(0));
    }

    public static class ExampleListFactory extends FactoryBase<Void, ExampleListFactory> {
        public StringListAttribute listAttribute =new StringListAttribute();
    }

    @Test
    public void testObservable(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleListFactory.listAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleListFactory.listAttribute.add("7787");

        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_json_in_data(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        exampleListFactory.listAttribute.add("7787");
        ObjectMapperBuilder.build().copy(exampleListFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<List<String>,StringListAttribute> invalidationListener = (a, o) -> {
            calls.add("");
        };
        exampleListFactory.listAttribute.internal_addListener(invalidationListener);
        exampleListFactory.listAttribute.add("7787");

        Assertions.assertEquals(1,calls.size());

        exampleListFactory.listAttribute.internal_removeListener(invalidationListener);
        exampleListFactory.listAttribute.add("7787");
        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_modify(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        exampleListFactory.internal().finalise();
        exampleListFactory.listAttribute.add("7787");
        Assertions.assertEquals(1,exampleListFactory.getModified().size());
        Assertions.assertTrue(exampleListFactory.getModified().contains(exampleListFactory));
    }

    @Test
    public void test_resetModification(){
        ExampleListFactory exampleFactory = new ExampleListFactory();
        exampleFactory.listAttribute.add("111");
        exampleFactory.internal().finalise();

        exampleFactory.listAttribute.add("222");
        exampleFactory.internal().resetModificationFlat();
        Assertions.assertEquals(List.of("111"),exampleFactory.listAttribute.get());
    }
}