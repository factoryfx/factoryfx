package io.github.factoryfx.javafx.util;

import io.github.factoryfx.factory.FactoryBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataObservableDisplayText {
    private SimpleStringProperty simpleStringProperty=new SimpleStringProperty();

    public DataObservableDisplayText(FactoryBase<?,?> data){
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
