package io.github.factoryfx.javafx;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.factory.util.DataObservableDisplayText;
import javafx.beans.property.ReadOnlyStringProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataObservableDisplayTextTest {

    private class ExampleFactoryObservable extends FactoryBase<Void,ExampleFactoryObservable> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
        public ExampleFactoryObservable(){
            config().setDisplayTextProvider(() -> stringAttribute.get());
            config().setDisplayTextDependencies(stringAttribute);
        }
    }


    @Test
    public void test_displaytext_observable(){
        ExampleFactoryObservable exampleFactory = new ExampleFactoryObservable();
        exampleFactory.stringAttribute.set("1");

        ReadOnlyStringProperty expected = new DataObservableDisplayText(exampleFactory).get();
        ReadOnlyStringProperty actual = new DataObservableDisplayText(exampleFactory).get();
        Assertions.assertEquals(expected, actual,"stable ref");

        Assertions.assertEquals("1",new DataObservableDisplayText(exampleFactory).get().get());
        exampleFactory.stringAttribute.set("2");
        Assertions.assertEquals("2",new DataObservableDisplayText(exampleFactory).get().get());
    }

    @Test
    public void test_displaytext_observable_changedetection(){
        ExampleFactoryObservable exampleFactory = new ExampleFactoryObservable();
        exampleFactory.stringAttribute.set("1");

        ReadOnlyStringProperty observable = new DataObservableDisplayText(exampleFactory).get();

        Assertions.assertEquals("1",observable.get());
        exampleFactory.stringAttribute.set("2");
        Assertions.assertEquals("2",observable.get());
    }

}