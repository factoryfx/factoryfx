package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import javafx.collections.ObservableSet;
import org.junit.Assert;
import org.junit.Test;

public class SetAttributeTest {

    public static class ExampleSetFactory extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
        public ValueSetAttribute<String> setAttribute =new ValueSetAttribute<>(new AttributeMetadata(),String.class,"empty");

        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void testObservable(){
        ExampleSetFactory exampleSetFactory = new ExampleSetFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleSetFactory.setAttribute.addListener((a,o)-> {
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
        AttributeChangeListener<ObservableSet<String>> invalidationListener = (a,o) -> {
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