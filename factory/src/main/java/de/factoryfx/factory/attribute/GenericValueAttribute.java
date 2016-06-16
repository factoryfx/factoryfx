package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class GenericValueAttribute<T> extends ValueAttribute<T> {
    @JsonCreator
    public GenericValueAttribute(T value) {
        set(value);
    }
}
