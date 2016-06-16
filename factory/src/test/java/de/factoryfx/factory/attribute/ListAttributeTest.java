package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.ExampleLiveObjectA;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

public class ListAttributeTest {

    public static class ExampleListFactory extends FactoryBase<ExampleLiveObjectA,ExampleFactoryA> {
        public ValueListAttribute<String> listAttribute =new ValueListAttribute<>();

        @Override
        protected ExampleLiveObjectA createImp(Optional<ExampleLiveObjectA> previousLiveObject) {
            return null;
        }
    }

    @Test
    public void testObservable(){
        ExampleListFactory exampleListFactory = new ExampleListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleListFactory.listAttribute.addListener((a,o)-> {
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
        AttributeChangeListener<ObservableList<String>> invalidationListener = (a,o) -> {
            calls.add("");
        };
        exampleListFactory.listAttribute.addListener(invalidationListener);
        exampleListFactory.listAttribute.get().add("7787");

        Assert.assertEquals(1,calls.size());

        exampleListFactory.listAttribute.removeListener(invalidationListener);
        exampleListFactory.listAttribute.get().add("7787");
        Assert.assertEquals(1,calls.size());
    }
}