package de.factoryfx.factory.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;

public class GenericValueAttribute<T> extends ValueAttribute<T,GenericValueAttribute<T>> {
    @JsonCreator
    GenericValueAttribute(T value) {
        super(null);
        set(value);
    }

    @JsonCreator
    public GenericValueAttribute(AttributeMetadata attributeMetadata) {
        super(attributeMetadata);
    }
}
