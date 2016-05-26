package de.factoryfx.model.attribute;

import java.util.ArrayList;

import de.factoryfx.model.ClosedPreviousLiveObject;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.jackson.ObjectMapperBuilder;
import de.factoryfx.model.testfactories.ExampleFactoryA;
import de.factoryfx.model.testfactories.ExampleLiveObjectA;
import javafx.beans.InvalidationListener;
import org.junit.Assert;
import org.junit.Test;

public class SetAttributeTest {

    public static class ExampleSetFactory extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
        public ValueSetAttribute<String> setAttribute =new ValueSetAttribute<>(new AttributeMetadata<>("ExampleA1"));

        @Override
        protected ExampleLiveObjectA createImp(ClosedPreviousLiveObject<ExampleLiveObjectA> closedPreviousLiveObject) {
            return null;
        }
    }

    @Test
    public void testObservable(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleSetFactory.setAttribute.addListener((o)-> {
            calls.add("");
        });
        exampleSetFactory.setAttribute.get().add("7787");

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        exampleSetFactory.setAttribute.get().add("7787");
        ObjectMapperBuilder.build().copy(exampleSetFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        ArrayList<String> calls= new ArrayList<>();
        InvalidationListener invalidationListener = (o) -> {
            calls.add("");
        };
        exampleSetFactory.setAttribute.addListener(invalidationListener);
        exampleSetFactory.setAttribute.get().add("7787");

        Assert.assertEquals(1,calls.size());

        exampleSetFactory.setAttribute.removeListener(invalidationListener);
        exampleSetFactory.setAttribute.get().add("7787");
        Assert.assertEquals(1,calls.size());
    }
}