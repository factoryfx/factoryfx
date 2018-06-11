package de.factoryfx.javafx.data.util;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.javafx.data.util.DataObservableDisplayText;
import javafx.beans.property.ReadOnlyStringProperty;
import org.junit.Assert;
import org.junit.Test;

public class DataObservableDisplayTextTest {

    private class ExampleFactoryObservable extends Data {
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
        Assert.assertEquals("stable ref", expected, actual);

        Assert.assertEquals("1",new DataObservableDisplayText(exampleFactory).get().get());
        exampleFactory.stringAttribute.set("2");
        Assert.assertEquals("2",new DataObservableDisplayText(exampleFactory).get().get());
    }

    @Test
    public void test_displaytext_observable_changedetection(){
        ExampleFactoryObservable exampleFactory = new ExampleFactoryObservable();
        exampleFactory.stringAttribute.set("1");

        ReadOnlyStringProperty observable = new DataObservableDisplayText(exampleFactory).get();

        Assert.assertEquals("1",observable.get());
        exampleFactory.stringAttribute.set("2");
        Assert.assertEquals("2",observable.get());
    }

}