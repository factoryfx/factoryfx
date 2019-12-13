package io.github.factoryfx.javafx.util;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeChangeListener;
import io.github.factoryfx.factory.attribute.WeakAttributeChangeListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableFactoryDisplayText implements ObservableValue<String> {

    private final SimpleStringProperty property=new SimpleStringProperty();
    private final AttributeChangeListener attributeChangeListener;

    @SuppressWarnings("unchecked")
    public ObservableFactoryDisplayText(FactoryBase<?,?> factory){
        attributeChangeListener = (attribute, value) -> property.set(factory.internal().getDisplayText());
        factory.internal().addDisplayTextListeners(new WeakAttributeChangeListener(attributeChangeListener));
        attributeChangeListener.changed(null,null);
    }

    @Override
    public void addListener(ChangeListener<? super String> listener) {
        property.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
        property.removeListener(listener);
    }

    @Override
    public String getValue() {
        return property.getValue();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        property.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        property.removeListener(listener);
    }
}
