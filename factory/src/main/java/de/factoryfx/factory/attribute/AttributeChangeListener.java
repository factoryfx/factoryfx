package de.factoryfx.factory.attribute;

public interface AttributeChangeListener<T> {
    void changed(Attribute<T,?> attribute, T value);
}
