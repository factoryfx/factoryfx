package io.github.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.Set;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.types.StringSetAttribute;
import io.github.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetAttributeTest {

    public static class ExampleSetFactory extends Data {
        public StringSetAttribute setAttribute =new StringSetAttribute();
    }

    @Test
    public void testObservable(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleSetFactory.setAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleSetFactory.setAttribute.add("7787");

        Assertions.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        exampleSetFactory.setAttribute.add("7787");
        ObjectMapperBuilder.build().copy(exampleSetFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<Set<String>,StringSetAttribute> invalidationListener = (a, o) -> {
            calls.add("");
        };
        exampleSetFactory.setAttribute.internal_addListener(invalidationListener);
        exampleSetFactory.setAttribute.add("7787");

        Assertions.assertEquals(1,calls.size());

        exampleSetFactory.setAttribute.internal_removeListener(invalidationListener);
        exampleSetFactory.setAttribute.add("7787");
        Assertions.assertEquals(1,calls.size());
    }
}