package de.factoryfx.data.attribute;

import java.util.ArrayList;

import de.factoryfx.data.attribute.util.StringMapAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class MapAttributeTest {

    public static class ExampleMapFactory extends IdData {
        public StringMapAttribute mapAttribute=new StringMapAttribute(new AttributeMetadata());
    }

    @Test
    public void testObservable(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleMapFactory.mapAttribute.addListener((a,o)-> {
            calls.add("");
        });
        exampleMapFactory.mapAttribute.get().put("123","7787");

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleMapFactory exampleMapFactory = new ExampleMapFactory();
        exampleMapFactory.mapAttribute.get().put("123","7787");
        ObjectMapperBuilder.build().copy(exampleMapFactory);
    }
}