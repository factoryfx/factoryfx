package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
        public ReferenceAttribute<ExampleFactoryA> referenceAttribute =new ReferenceAttribute<>(ExampleFactoryA.class,new AttributeMetadata());
        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
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
        AttributeChangeListener<ExampleFactoryA> invalidationListener = (a,o) -> {
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
}