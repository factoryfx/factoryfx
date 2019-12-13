package io.github.factoryfx.javafx;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.javafx.util.ObservableFactoryDisplayText;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObservableFactoryDisplayTextTest {

    private static class ExampleFactoryObservable extends FactoryBase<Void,ExampleFactoryObservable> {
        public final StringAttribute stringAttribute= new StringAttribute();
        public ExampleFactoryObservable(){
            config().setDisplayTextProvider(stringAttribute::get);
            config().setDisplayTextDependencies(stringAttribute);
        }
    }

    @Test
    public void test_displaytext_observable_changedetection(){
        ExampleFactoryObservable exampleFactory = new ExampleFactoryObservable();
        exampleFactory.stringAttribute.set("1");

        ObservableValue<String> observable = new ObservableFactoryDisplayText(exampleFactory);

        Assertions.assertEquals("1",observable.getValue());
        exampleFactory.stringAttribute.set("2");
        Assertions.assertEquals("2",observable.getValue());
    }

}