package io.github.factoryfx.factory.attribute;

public interface AttributeValue<V> {

    V get();
    void set(V value);
}
