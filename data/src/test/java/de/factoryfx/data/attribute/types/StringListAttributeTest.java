package de.factoryfx.data.attribute.types;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class StringListAttributeTest {

    public static class ExampleListFactory extends Data {
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

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
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

        Assert.assertEquals(1,calls.size());

        exampleListFactory.listAttribute.internal_removeListener(invalidationListener);
        exampleListFactory.listAttribute.add("7787");
        Assert.assertEquals(1,calls.size());
    }
}