package io.github.factoryfx.javafx.data.util;

import io.github.factoryfx.data.Data;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataObservableDisplayText {
    private SimpleStringProperty simpleStringProperty=new SimpleStringProperty();

    public DataObservableDisplayText(Data data){
        if (data.internal().getDisplayTextObservable() instanceof SimpleStringProperty){
            simpleStringProperty=(SimpleStringProperty)data.internal().getDisplayTextObservable();
        }

        simpleStringProperty.set(data.internal().getDisplayText());
        data.internal().addDisplayTextListeners((attributeParam, value) -> simpleStringProperty.set(data.internal().getDisplayText()));
        data.internal().storeDisplayTextObservable(simpleStringProperty);
    }

    public ReadOnlyStringProperty get(){
        return simpleStringProperty;
    }
}
