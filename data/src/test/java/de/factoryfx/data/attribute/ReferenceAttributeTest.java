package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.IdData;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends IdData {
        public ReferenceAttribute<ExampleFactoryA> referenceAttribute =new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
    }

    @Test
    public void testObservable(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        ArrayList<String> calls= new ArrayList<>();
        referenceAttribute.addListener((a,value) -> calls.add(""));
        referenceAttribute.set(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceFactory);
    }

    @Test
    public void remove_Listener(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleFactoryA> invalidationListener = (a, o) -> {
            calls.add("");
        };
        referenceAttribute.addListener(invalidationListener);
        referenceAttribute.set(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        referenceAttribute.removeListener(invalidationListener);
        referenceAttribute.set(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        Assert.assertNull(referenceAttribute.get());
        referenceAttribute.addNewFactory();
        Assert.assertNotNull(referenceAttribute.get());

    }

    @Test
    public void test_get_possible(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        ExampleReferenceFactory root = new ExampleReferenceFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        root.referenceAttribute.set(exampleFactoryA);

        referenceAttribute.prepareEditing(root,null);

        List<ExampleFactoryA> possibleFactories =referenceAttribute.possibleValues();
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA,possibleFactories.get(0));

    }

    @Test
    public void test_Observable_first(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        ArrayList<Object> calls= new ArrayList<>();
        referenceAttribute.addListener((a,value) -> calls.add(value));
        ExampleFactoryA added = new ExampleFactoryA();
        referenceAttribute.set(added);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals(added,calls.get(0));
    }

    @Test
    public void test_listener(){
        ReferenceAttribute<ExampleFactoryA> referenceAttribute=new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        List<ExampleFactoryA> calls = new ArrayList<>();
        List<ExampleFactoryA> callsAttributeGet = new ArrayList<>();
        referenceAttribute.addListener((attribute, value) -> {
            calls.add(value);
            callsAttributeGet.add(attribute.get());
        });
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        referenceAttribute.set(exampleFactoryA);
        Assert.assertEquals(1,calls.size());
        Assert.assertEquals(exampleFactoryA,calls.get(0));
        Assert.assertEquals(exampleFactoryA,callsAttributeGet.get(0));

    }


}