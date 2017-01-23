package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ListAttributeTest {

    public static class ExampleListFactory extends IdData {
        public StringListAttribute listAttribute =new StringListAttribute(new AttributeMetadata());
    }

    @Test
    public void testObservable(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleListFactory.listAttribute.internal_addListener((a,o)-> {
            calls.add("");
        });
        exampleListFactory.listAttribute.get().add("7787");

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        exampleListFactory.listAttribute.get().add("7787");
        ObjectMapperBuilder.build().copy(exampleListFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<List<String>> invalidationListener = (a, o) -> {
            calls.add("");
        };
        exampleListFactory.listAttribute.internal_addListener(invalidationListener);
        exampleListFactory.listAttribute.get().add("7787");

        Assert.assertEquals(1,calls.size());

        exampleListFactory.listAttribute.internal_removeListener(invalidationListener);
        exampleListFactory.listAttribute.get().add("7787");
        Assert.assertEquals(1,calls.size());
    }
}