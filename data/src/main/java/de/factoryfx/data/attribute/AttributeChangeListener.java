package de.factoryfx.data.attribute;

public interface AttributeChangeListener<T> {
    void changed(Attribute<T> attribute, T value);
}