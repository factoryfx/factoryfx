package de.factoryfx.data.attribute;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleFactoryA;
import de.factoryfx.data.merge.testfactories.IdData;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceListAttributeTest {

    public static class ExampleReferenceListFactory extends IdData {
        public ReferenceListAttribute<ExampleFactoryA> referenceListAttribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());


    }

    @Test
    public void testObservable(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceListFactory.referenceListAttribute.addListener((a,o)-> {
            calls.add("");
        });
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceListFactory.referenceListAttribute.get().add(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceListFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ObservableList<ExampleFactoryA>> invalidationListener = (a,o) -> {
            calls.add("");
        };
        exampleReferenceListFactory.referenceListAttribute.addListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceListFactory.referenceListAttribute.removeListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        exampleReferenceListFactory.referenceListAttribute.prepareUsage(new ExampleReferenceListFactory(),null);
        exampleReferenceListFactory.referenceListAttribute.addNewFactory();
        Assert.assertEquals(1,exampleReferenceListFactory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        exampleReferenceListFactory.internal().prepareUsage();


        List<ExampleFactoryA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.possibleValues();
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA,possibleFactories.get(0));

    }

    @Test
    public void test_add_new_listener(){
        ReferenceListAttribute<ExampleFactoryA> referenceListAttribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        referenceListAttribute.prepareUsage(new ExampleFactoryA(),null);
        List<ExampleFactoryA> calls=new ArrayList<>();
        referenceListAttribute.addListener((attribute, value) -> calls.add(value.get(0)));
        referenceListAttribute.addNewFactory();
        Assert.assertEquals(1,calls.size());
        referenceListAttribute.addNewFactory();
        Assert.assertEquals(2,calls.size());
//        Assert.assertEquals(value,calls.get(0));
    }
}