package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PreviousLiveObjectProvider;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import javafx.beans.InvalidationListener;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceAttributeTest {

    public static class ExampleReferenceFactory extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
        public ReferenceAttribute<ExampleFactoryA> referenceAttribute =new ReferenceAttribute<ExampleFactoryA>(new AttributeMetadata<>("ExampleA1"));
        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject, PreviousLiveObjectProvider previousLiveObjectProvider) {
            return null;
        }
    }

    @Test
    public void testObservable(){
        ExampleReferenceFactory exampleReferenceFactory = new ExampleReferenceFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceFactory.referenceAttribute.addListener((o)-> {
            calls.add("");
        });
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
        InvalidationListener invalidationListener = (o) -> {
            calls.add("");
        };
        exampleReferenceFactory.referenceAttribute.addListener(invalidationListener);
        exampleReferenceFactory.referenceAttribute.set(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceFactory.referenceAttribute.removeListener(invalidationListener);
        exampleReferenceFactory.referenceAttribute.set(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }
}