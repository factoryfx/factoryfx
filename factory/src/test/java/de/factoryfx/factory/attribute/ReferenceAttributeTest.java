package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import de.factoryfx.factory.util.VoidLiveObject;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends FactoryBase<VoidLiveObject,ExampleReferenceFactory> {
        public ReferenceAttribute<ExampleLiveObjectA,ExampleFactoryA> referenceAttribute =new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        @Override
        protected VoidLiveObject createImp(Optional<VoidLiveObject> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void testObservable(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceFactory.referenceAttribute.addListener((a,value) -> calls.add(""));
        exampleReferenceFactory.referenceAttribute.set(new ExampleFactoryA());

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
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ExampleFactoryA> invalidationListener = (a, o) -> {
            calls.add("");
        };
        exampleReferenceFactory.referenceAttribute.addListener(invalidationListener);
        exampleReferenceFactory.referenceAttribute.set(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceFactory.referenceAttribute.removeListener(invalidationListener);
        exampleReferenceFactory.referenceAttribute.set(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        Assert.assertNull(exampleReferenceFactory.referenceAttribute.get());
        exampleReferenceFactory.referenceAttribute.addNewFactory(null);
        Assert.assertNotNull(exampleReferenceFactory.referenceAttribute.get());

    }

    @Test
    public void test_get_possible(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleReferenceFactory.referenceAttribute.set(exampleFactoryA);

        List<ExampleFactoryA> possibleFactories = exampleReferenceFactory.referenceAttribute.possibleValues(exampleReferenceFactory);
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA,possibleFactories.get(0));

    }
}